package org.dre0065.Repository;

import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.WeightCategory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public class MMAFighterRepository
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<MMAFighter> fighterRowMapper = (rs, rowNum) ->
    {
        MMAFighter f = new MMAFighter();
        f.setFighterId(rs.getInt("fighter_id"));
        f.setFirstName(rs.getString("first_name"));
        f.setLastName(rs.getString("last_name"));
        f.setWeight(rs.getString("weight"));
        f.setHeight(rs.getString("height"));
        f.setReach(rs.getString("reach"));
        f.setNationality(rs.getString("nationality"));
        f.setRanking(rs.getString("ranking"));
        f.setFights(rs.getInt("fights"));
        f.setPoints(rs.getInt("points"));

        WeightCategory wc = new WeightCategory();
        wc.setWeightCategoryId(rs.getInt("wc_id"));
        wc.setName(rs.getString("wc_name"));
        wc.setMinWeight(rs.getString("wc_min_weight"));
        wc.setMaxWeight(rs.getString("wc_max_weight"));

        f.setWeightCategory(wc);
        return f;
    };

    public boolean existsByFirstNameAndLastName(String firstName, String lastName)
    {
        String sql = "SELECT COUNT(*) FROM mma_fighter WHERE first_name=? AND last_name=?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, firstName, lastName);
        return count != null && count > 0;
    }

    public Optional<MMAFighter> findByFirstNameAndLastName(String firstName, String lastName)
    {
        String sql = "SELECT f.fighter_id, f.first_name, f.last_name, f.weight, f.height, f.reach, f.nationality, f.ranking, f.fights, f.points, w.weight_category_id AS wc_id, w.name AS wc_name, w.min_weight AS wc_min_weight, w.max_weight AS wc_max_weight " +
                "FROM mma_fighter f JOIN weight_category w ON f.weight_category_id = w.weight_category_id " +
                "WHERE f.first_name=? AND f.last_name=?";
        List<MMAFighter> results = jdbcTemplate.query(sql, fighterRowMapper, firstName, lastName);
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<MMAFighter> findById(Integer id)
    {
        String sql = "SELECT f.fighter_id, f.first_name, f.last_name, f.weight, f.height, f.reach, f.nationality, f.ranking, f.fights, f.points, w.weight_category_id AS wc_id, w.name AS wc_name, w.min_weight AS wc_min_weight, w.max_weight AS wc_max_weight " +
                "FROM mma_fighter f JOIN weight_category w ON f.weight_category_id = w.weight_category_id " +
                "WHERE f.fighter_id=?";
        List<MMAFighter> results = jdbcTemplate.query(sql, fighterRowMapper, id);
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public List<MMAFighter> findAll()
    {
        String sql = "SELECT f.fighter_id, f.first_name, f.last_name, f.weight, f.height, f.reach, f.nationality, f.ranking, f.fights, f.points, w.weight_category_id AS wc_id, w.name AS wc_name, w.min_weight AS wc_min_weight, w.max_weight AS wc_max_weight " +
                "FROM mma_fighter f JOIN weight_category w ON f.weight_category_id = w.weight_category_id";
        return jdbcTemplate.query(sql, fighterRowMapper);
    }

    public MMAFighter save(MMAFighter fighter)
    {
        if(fighter.getFighterId() == 0)
        {
            String sql = "INSERT INTO mma_fighter (first_name, last_name, weight, height, reach, nationality, ranking, fights, points, weight_category_id) VALUES (?,?,?,?,?,?,?,?,?,?)";
            jdbcTemplate.update(sql, fighter.getFirstName(), fighter.getLastName(), fighter.getWeight(), fighter.getHeight(), fighter.getReach(), fighter.getNationality(), fighter.getRanking(), fighter.getFights(), fighter.getPoints(), fighter.getWeightCategory().getWeightCategoryId());
            Integer newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            if(newId != null) fighter.setFighterId(newId);
            return fighter;
        }
        else
        {
            String sql = "UPDATE mma_fighter SET first_name=?, last_name=?, weight=?, height=?, reach=?, nationality=?, ranking=?, fights=?, points=?, weight_category_id=? WHERE fighter_id=?";
            jdbcTemplate.update(sql, fighter.getFirstName(), fighter.getLastName(), fighter.getWeight(), fighter.getHeight(), fighter.getReach(), fighter.getNationality(), fighter.getRanking(), fighter.getFights(), fighter.getPoints(), fighter.getWeightCategory().getWeightCategoryId(), fighter.getFighterId());
            return fighter;
        }
    }

    public void saveAll(List<MMAFighter> fighters) {for(MMAFighter f : fighters) save(f);}

    public void deleteById(Integer id)
    {
        String sql = "DELETE FROM mma_fighter WHERE fighter_id=?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(MMAFighter fighter) {deleteById(fighter.getFighterId());}

    public void deleteByWeightCategoryId(int weightCategoryId)
    {
        String sql = "DELETE FROM mma_fighter WHERE weight_category_id = ?";
        jdbcTemplate.update(sql, weightCategoryId);
    }
}