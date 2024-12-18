package org.dre0065.Repository;

import org.dre0065.Model.Preparation;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.Coach;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

@Repository
public class PreparationRepository
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Preparation> preparationRowMapper = (rs, rowNum) ->
    {
        Preparation p = new Preparation();
        p.setPreparationId(rs.getInt("preparation_id"));
        p.setStartOfPreparation(rs.getTimestamp("start_of_preparation"));
        p.setEndOfPreparation(rs.getTimestamp("end_of_preparation"));
        p.setMmaClub(rs.getString("mma_club"));
        p.setClubRegion(rs.getString("club_region"));

        MMAFighter f = new MMAFighter();
        f.setFighterId(rs.getInt("fighter_id"));
        f.setFirstName(rs.getString("f_fname"));
        f.setLastName(rs.getString("f_lname"));

        Coach c = new Coach();
        c.setCoachId(rs.getInt("coach_id"));
        c.setFirstName(rs.getString("c_fname"));
        c.setLastName(rs.getString("c_lname"));
        c.setSpecialization(rs.getString("c_specialization"));

        p.setFighter(f);
        p.setCoach(c);
        return p;
    };

    public Preparation findByStartOfPreparationAndEndOfPreparationAndFighterAndCoach(Date startOfPreparation, Date endOfPreparation, MMAFighter fighter, Coach coach)
    {
        String sql = "SELECT p.preparation_id, p.start_of_preparation, p.end_of_preparation, p.mma_club, p.club_region, p.fighter_id, p.coach_id, f.first_name AS f_fname, f.last_name AS f_lname, c.first_name AS c_fname, c.last_name AS c_lname, c.specialization AS c_specialization " +
                "FROM preparation p JOIN mma_fighter f ON p.fighter_id = f.fighter_id JOIN coach c ON p.coach_id = c.coach_id " +
                "WHERE p.start_of_preparation=? AND p.end_of_preparation=? AND p.fighter_id=? AND p.coach_id=?";
        List<Preparation> results = jdbcTemplate.query(sql, preparationRowMapper, new Timestamp(startOfPreparation.getTime()), new Timestamp(endOfPreparation.getTime()), fighter.getFighterId(), coach.getCoachId());
        if(results.isEmpty()) return null;
        return results.get(0);
    }

    public boolean existsByCoach(Coach coach)
    {
        String sql = "SELECT COUNT(*) FROM preparation WHERE coach_id=?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, coach.getCoachId());
        return count != null && count > 0;
    }

    public void deleteByCoach(Coach coach)
    {
        String sql = "DELETE FROM preparation WHERE coach_id=?";
        jdbcTemplate.update(sql, coach.getCoachId());
    }

    public Optional<Preparation> findById(Integer id)
    {
        String sql = "SELECT p.preparation_id, p.start_of_preparation, p.end_of_preparation, p.mma_club, p.club_region, p.fighter_id, p.coach_id, f.first_name AS f_fname, f.last_name AS f_lname, c.first_name AS c_fname, c.last_name AS c_lname, c.specialization AS c_specialization " +
                "FROM preparation p JOIN mma_fighter f ON p.fighter_id = f.fighter_id JOIN coach c ON p.coach_id = c.coach_id " +
                "WHERE p.preparation_id=?";
        List<Preparation> results = jdbcTemplate.query(sql, preparationRowMapper, id);
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public List<Preparation> findAll()
    {
        String sql = "SELECT p.preparation_id, p.start_of_preparation, p.end_of_preparation, p.mma_club, p.club_region, p.fighter_id, p.coach_id, f.first_name AS f_fname, f.last_name AS f_lname, c.first_name AS c_fname, c.last_name AS c_lname, c.specialization AS c_specialization " +
                "FROM preparation p JOIN mma_fighter f ON p.fighter_id = f.fighter_id JOIN coach c ON p.coach_id = c.coach_id";
        return jdbcTemplate.query(sql, preparationRowMapper);
    }

    public Preparation save(Preparation preparation)
    {
        if(preparation.getPreparationId() == 0) {
            String sql = "INSERT INTO preparation (start_of_preparation, end_of_preparation, mma_club, club_region, fighter_id, coach_id) VALUES (?,?,?,?,?,?)";
            jdbcTemplate.update(sql, new Timestamp(preparation.getStartOfPreparation().getTime()), new Timestamp(preparation.getEndOfPreparation().getTime()), preparation.getMmaClub(), preparation.getClubRegion(), preparation.getFighter().getFighterId(), preparation.getCoach().getCoachId());
            Integer newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            if(newId != null) preparation.setPreparationId(newId);
            return preparation;
        }
        else
        {
            String sql = "UPDATE preparation SET start_of_preparation=?, end_of_preparation=?, mma_club=?, club_region=?, fighter_id=?, coach_id=? WHERE preparation_id=?";
            jdbcTemplate.update(sql, new Timestamp(preparation.getStartOfPreparation().getTime()), new Timestamp(preparation.getEndOfPreparation().getTime()), preparation.getMmaClub(), preparation.getClubRegion(), preparation.getFighter().getFighterId(), preparation.getCoach().getCoachId(), preparation.getPreparationId());
            return preparation;
        }
    }

    public void saveAll(List<Preparation> preparations) {for(Preparation p : preparations) save(p);}

    public void deleteById(Integer id)
    {
        String sql = "DELETE FROM preparation WHERE preparation_id=?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Preparation preparation) {deleteById(preparation.getPreparationId());}

    public void deleteByFighterId(int fighterId)
    {
        String sql = "DELETE FROM preparation WHERE fighter_id = ?";
        jdbcTemplate.update(sql, fighterId);
    }

    public void deleteByWeightCategoryId(int weightCategoryId)
    {
        String sql = "DELETE FROM preparation WHERE fighter_id IN (SELECT fighter_id FROM mma_fighter WHERE weight_category_id=?)";
        jdbcTemplate.update(sql, weightCategoryId);
    }
}