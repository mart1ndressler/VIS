package org.dre0065.Repository;

import org.dre0065.Model.Event;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

@Repository
public class EventRepository
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Event> eventRowMapper = new RowMapper<Event>()
    {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            Event e = new Event();
            e.setEventId(rs.getInt("event_id"));
            e.setEventName(rs.getString("event_name"));
            e.setMmaOrganization(rs.getString("mma_organization"));
            e.setStartOfEvent(rs.getTimestamp("start_of_event"));
            e.setEndOfEvent(rs.getTimestamp("end_of_event"));
            e.setLocation(rs.getString("location"));
            return e;
        }
    };

    public boolean existsByEventNameAndStartOfEvent(String eventName, Date startOfEvent)
    {
        String sql = "SELECT COUNT(*) FROM event WHERE event_name=? AND start_of_event=?";
        java.sql.Timestamp startTs = new java.sql.Timestamp(startOfEvent.getTime());
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, eventName, startTs);
        return count != null && count > 0;
    }

    public List<Event> findByEventName(String eventName)
    {
        String sql = "SELECT * FROM event WHERE event_name = ?";
        return jdbcTemplate.query(sql, eventRowMapper, eventName);
    }

    public Optional<Event> findById(Integer id)
    {
        String sql = "SELECT * FROM event WHERE event_id = ?";
        List<Event> results = jdbcTemplate.query(sql, eventRowMapper, id);
        if (results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public List<Event> findAll()
    {
        String sql = "SELECT * FROM event";
        return jdbcTemplate.query(sql, eventRowMapper);
    }

    public Event save(Event event)
    {
        if(event.getEventId() == 0)
        {
            String sql = "INSERT INTO event (event_name, mma_organization, start_of_event, end_of_event, location) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, event.getEventName(), event.getMmaOrganization(), new java.sql.Timestamp(event.getStartOfEvent().getTime()), new java.sql.Timestamp(event.getEndOfEvent().getTime()), event.getLocation());
            Integer newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            if(newId != null) event.setEventId(newId);
            return event;
        }
        else
        {
            String sql = "UPDATE event SET event_name=?, mma_organization=?, start_of_event=?, end_of_event=?, location=? WHERE event_id=?";
            jdbcTemplate.update(sql, event.getEventName(), event.getMmaOrganization(), new java.sql.Timestamp(event.getStartOfEvent().getTime()), new java.sql.Timestamp(event.getEndOfEvent().getTime()), event.getLocation(), event.getEventId());
            return event;
        }
    }

    public void saveAll(List<Event> events) {for(Event event : events) save(event);}

    public void deleteById(Integer id)
    {
        String sql = "DELETE FROM event WHERE event_id = ?";
        jdbcTemplate.update(sql, id);
    }
}