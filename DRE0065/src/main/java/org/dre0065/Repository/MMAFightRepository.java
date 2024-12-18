package org.dre0065.Repository;

import org.dre0065.Model.MMAFight;
import org.dre0065.Model.Fight;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Model.Event;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public class MMAFightRepository
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<MMAFight> mmaFightRowMapper = (rs, rowNum) ->
    {
        MMAFight mm = new MMAFight();
        mm.setMmaFightId(rs.getInt("mma_fight_id"));

        Fight fight = new Fight();
        fight.setFightId(rs.getInt("fight_id"));
        fight.setDate(rs.getTimestamp("f_date"));
        fight.setResult(rs.getString("f_result"));
        fight.setTypeOfResult(rs.getString("f_type_of_result"));

        WeightCategory wc = new WeightCategory();
        wc.setWeightCategoryId(rs.getInt("wc_id"));
        wc.setName(rs.getString("wc_name"));
        wc.setMinWeight(rs.getString("wc_min_weight"));
        wc.setMaxWeight(rs.getString("wc_max_weight"));
        fight.setWeightCategory(wc);

        Event ev = new Event();
        ev.setEventId(rs.getInt("ev_id"));
        ev.setEventName(rs.getString("ev_name"));
        ev.setMmaOrganization(rs.getString("ev_mma_organization"));
        ev.setStartOfEvent(rs.getTimestamp("ev_start"));
        ev.setEndOfEvent(rs.getTimestamp("ev_end"));
        ev.setLocation(rs.getString("ev_location"));
        fight.setEvent(ev);

        mm.setFight(fight);

        MMAFighter f = new MMAFighter();
        f.setFighterId(rs.getInt("fi_id"));
        f.setFirstName(rs.getString("fi_first_name"));
        f.setLastName(rs.getString("fi_last_name"));
        f.setWeight(rs.getString("fi_weight"));
        f.setHeight(rs.getString("fi_height"));
        f.setReach(rs.getString("fi_reach"));
        f.setNationality(rs.getString("fi_nationality"));
        f.setRanking(rs.getString("fi_ranking"));
        f.setFights(rs.getInt("fi_fights"));
        f.setPoints(rs.getInt("fi_points"));

        WeightCategory fwc = new WeightCategory();
        fwc.setWeightCategoryId(rs.getInt("fi_wc_id"));
        fwc.setName(rs.getString("fi_wc_name"));
        fwc.setMinWeight(rs.getString("fi_wc_min_weight"));
        fwc.setMaxWeight(rs.getString("fi_wc_max_weight"));
        f.setWeightCategory(fwc);

        mm.setFighter(f);
        return mm;
    };

    public Optional<MMAFight> findByFightAndFighter(Fight fight, MMAFighter fighter)
    {
        String sql = "SELECT m.mma_fight_id, m.fight_id, m.fighter_id, f.date as f_date, f.result as f_result, f.type_of_result as f_type_of_result, w.weight_category_id as wc_id, w.name as wc_name, w.min_weight as wc_min_weight, w.max_weight as wc_max_weight, e.event_id as ev_id, e.event_name as ev_name, e.mma_organization as ev_mma_organization, e.start_of_event as ev_start, e.end_of_event as ev_end, e.location as ev_location, fi.fighter_id as fi_id, fi.first_name as fi_first_name, fi.last_name as fi_last_name, fi.weight as fi_weight, fi.height as fi_height, fi.reach as fi_reach, fi.nationality as fi_nationality, fi.ranking as fi_ranking, fi.fights as fi_fights, fi.points as fi_points, fiw.weight_category_id as fi_wc_id, fiw.name as fi_wc_name, fiw.min_weight as fi_wc_min_weight, fiw.max_weight as fi_wc_max_weight " +
                "FROM mma_fight m JOIN fight f ON m.fight_id = f.fight_id JOIN weight_category w ON f.weight_category_id = w.weight_category_id JOIN event e ON f.event_id = e.event_id JOIN mma_fighter fi ON m.fighter_id = fi.fighter_id JOIN weight_category fiw ON fi.weight_category_id = fiw.weight_category_id " +
                "WHERE m.fight_id=? AND m.fighter_id=?";
        List<MMAFight> results = jdbcTemplate.query(sql, mmaFightRowMapper, fight.getFightId(), fighter.getFighterId());
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<MMAFight> findById(Integer id)
    {
        String sql = "SELECT m.mma_fight_id, m.fight_id, m.fighter_id, f.date as f_date, f.result as f_result, f.type_of_result as f_type_of_result, w.weight_category_id as wc_id, w.name as wc_name, w.min_weight as wc_min_weight, w.max_weight as wc_max_weight, e.event_id as ev_id, e.event_name as ev_name, e.mma_organization as ev_mma_organization, e.start_of_event as ev_start, e.end_of_event as ev_end, e.location as ev_location, fi.fighter_id as fi_id, fi.first_name as fi_first_name, fi.last_name as fi_last_name, fi.weight as fi_weight, fi.height as fi_height, fi.reach as fi_reach, fi.nationality as fi_nationality, fi.ranking as fi_ranking, fi.fights as fi_fights, fi.points as fi_points, fiw.weight_category_id as fi_wc_id, fiw.name as fi_wc_name, fiw.min_weight as fi_wc_min_weight, fiw.max_weight as fi_wc_max_weight " +
                "FROM mma_fight m JOIN fight f ON m.fight_id = f.fight_id JOIN weight_category w ON f.weight_category_id = w.weight_category_id JOIN event e ON f.event_id = e.event_id JOIN mma_fighter fi ON m.fighter_id = fi.fighter_id JOIN weight_category fiw ON fi.weight_category_id = fiw.weight_category_id " +
                "WHERE m.mma_fight_id=?";
        List<MMAFight> results = jdbcTemplate.query(sql, mmaFightRowMapper, id);
        if (results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public List<MMAFight> findAll()
    {
        String sql = "SELECT m.mma_fight_id, m.fight_id, m.fighter_id, f.date as f_date, f.result as f_result, f.type_of_result as f_type_of_result, w.weight_category_id as wc_id, w.name as wc_name, w.min_weight as wc_min_weight, w.max_weight as wc_max_weight, e.event_id as ev_id, e.event_name as ev_name, e.mma_organization as ev_mma_organization, e.start_of_event as ev_start, e.end_of_event as ev_end, e.location as ev_location, fi.fighter_id as fi_id, fi.first_name as fi_first_name, fi.last_name as fi_last_name, fi.weight as fi_weight, fi.height as fi_height, fi.reach as fi_reach, fi.nationality as fi_nationality, fi.ranking as fi_ranking, fi.fights as fi_fights, fi.points as fi_points, fiw.weight_category_id as fi_wc_id, fiw.name as fi_wc_name, fiw.min_weight as fi_wc_min_weight, fiw.max_weight as fi_wc_max_weight " +
                "FROM mma_fight m JOIN fight f ON m.fight_id = f.fight_id JOIN weight_category w ON f.weight_category_id = w.weight_category_id JOIN event e ON f.event_id = e.event_id JOIN mma_fighter fi ON m.fighter_id = fi.fighter_id JOIN weight_category fiw ON fi.weight_category_id = fiw.weight_category_id";
        return jdbcTemplate.query(sql, mmaFightRowMapper);
    }

    public MMAFight save(MMAFight mmaFight)
    {
        if(mmaFight.getMmaFightId() == 0)
        {
            String sql = "INSERT INTO mma_fight (fight_id, fighter_id) VALUES (?,?)";
            jdbcTemplate.update(sql, mmaFight.getFight().getFightId(), mmaFight.getFighter().getFighterId());
            Integer newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            if(newId != null) mmaFight.setMmaFightId(newId);
            return mmaFight;
        }
        else
        {
            String sql = "UPDATE mma_fight SET fight_id=?, fighter_id=? WHERE mma_fight_id=?";
            jdbcTemplate.update(sql, mmaFight.getFight().getFightId(), mmaFight.getFighter().getFighterId(), mmaFight.getMmaFightId());
            return mmaFight;
        }
    }

    public void saveAll(List<MMAFight> mmaFights) {for(MMAFight mmf : mmaFights) save(mmf);}

    public void deleteById(Integer id)
    {
        String sql = "DELETE FROM mma_fight WHERE mma_fight_id=?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(MMAFight mmaFight) {deleteById(mmaFight.getMmaFightId());}

    public void deleteByFighterId(int fighterId)
    {
        String sql = "DELETE FROM mma_fight WHERE fighter_id = ?";
        jdbcTemplate.update(sql, fighterId);
    }

    public void deleteByFightId(int fightId)
    {
        String sql = "DELETE FROM mma_fight WHERE fight_id = ?";
        jdbcTemplate.update(sql, fightId);
    }

    public void deleteByWeightCategoryId(int weightCategoryId)
    {
        String sql = "DELETE FROM mma_fight WHERE fighter_id IN (SELECT fighter_id FROM mma_fighter WHERE weight_category_id=?)";
        jdbcTemplate.update(sql, weightCategoryId);
    }
}