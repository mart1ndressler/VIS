package org.dre0065.Repository;

import org.dre0065.Model.Event;
import org.dre0065.Model.Fight;
import org.dre0065.Model.WeightCategory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

@Repository
public class FightRepository
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Fight> fightRowMapper = (rs, rowNum) ->
    {
        Fight f = new Fight();
        f.setFightId(rs.getInt("fight_id"));
        f.setDate(rs.getTimestamp("date"));
        f.setResult(rs.getString("result"));
        f.setTypeOfResult(rs.getString("type_of_result"));

        WeightCategory wc = new WeightCategory();
        wc.setWeightCategoryId(rs.getInt("wc_id"));
        wc.setName(rs.getString("wc_name"));
        wc.setMinWeight(rs.getString("wc_min_weight"));
        wc.setMaxWeight(rs.getString("wc_max_weight"));

        Event e = new Event();
        e.setEventId(rs.getInt("e_id"));
        e.setEventName(rs.getString("event_name"));
        e.setMmaOrganization(rs.getString("mma_organization"));
        e.setStartOfEvent(rs.getTimestamp("start_of_event"));
        e.setEndOfEvent(rs.getTimestamp("end_of_event"));
        e.setLocation(rs.getString("location"));

        f.setWeightCategory(wc);
        f.setEvent(e);
        return f;
    };

    public Optional<Fight> findByDateAndWeightCategoryAndEvent(Date date, WeightCategory weightCategory, Event event)
    {
        String sql = "SELECT f.fight_id, f.date, f.result, f.type_of_result, w.weight_category_id AS wc_id, w.name AS wc_name, w.min_weight AS wc_min_weight, w.max_weight AS wc_max_weight, e.event_id AS e_id, e.event_name, e.mma_organization, e.start_of_event, e.end_of_event, e.location " +
                "FROM fight f JOIN weight_category w ON f.weight_category_id = w.weight_category_id JOIN event e ON f.event_id = e.event_id " +
                "WHERE f.date=? AND f.weight_category_id=? AND f.event_id=?";
        List<Fight> results = jdbcTemplate.query(sql, fightRowMapper, new Timestamp(date.getTime()), weightCategory.getWeightCategoryId(), event.getEventId());
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<Fight> findById(Integer id) {
        String sql = "SELECT f.fight_id, f.date, f.result, f.type_of_result, w.weight_category_id AS wc_id, w.name AS wc_name, w.min_weight AS wc_min_weight, w.max_weight AS wc_max_weight, e.event_id AS e_id, e.event_name, e.mma_organization, e.start_of_event, e.end_of_event, e.location " +
                "FROM fight f JOIN weight_category w ON f.weight_category_id = w.weight_category_id JOIN event e ON f.event_id = e.event_id " +
                "WHERE f.fight_id=?";
        List<Fight> results = jdbcTemplate.query(sql, fightRowMapper, id);
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public List<Fight> findAll()
    {
        String sql = "SELECT f.fight_id, f.date, f.result, f.type_of_result, w.weight_category_id AS wc_id, w.name AS wc_name, w.min_weight AS wc_min_weight, w.max_weight AS wc_max_weight, e.event_id AS e_id, e.event_name, e.mma_organization, e.start_of_event, e.end_of_event, e.location " +
                "FROM fight f JOIN weight_category w ON f.weight_category_id = w.weight_category_id JOIN event e ON f.event_id = e.event_id";
        return jdbcTemplate.query(sql, fightRowMapper);
    }

    public Fight save(Fight fight)
    {
        if(fight.getFightId() == 0)
        {
            String sql = "INSERT INTO fight (date, result, type_of_result, weight_category_id, event_id) VALUES (?,?,?,?,?)";
            jdbcTemplate.update(sql, new Timestamp(fight.getDate().getTime()), fight.getResult(), fight.getTypeOfResult(), fight.getWeightCategory().getWeightCategoryId(), fight.getEvent().getEventId());
            Integer newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            if(newId != null) fight.setFightId(newId);
            return fight;
        }
        else
        {
            String sql = "UPDATE fight SET date=?, result=?, type_of_result=?, weight_category_id=?, event_id=? WHERE fight_id=?";
            jdbcTemplate.update(sql, new Timestamp(fight.getDate().getTime()), fight.getResult(), fight.getTypeOfResult(), fight.getWeightCategory().getWeightCategoryId(), fight.getEvent().getEventId(), fight.getFightId());
            return fight;
        }
    }

    public void saveAll(List<Fight> fights) {for(Fight f : fights) save(f);}

    public void deleteById(Integer id)
    {
        String sql = "DELETE FROM fight WHERE fight_id=?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Fight fight) {deleteById(fight.getFightId());}

    public void deleteByWeightCategoryId(int weightCategoryId)
    {
        String sql = "DELETE FROM fight WHERE weight_category_id = ?";
        jdbcTemplate.update(sql, weightCategoryId);
    }
}