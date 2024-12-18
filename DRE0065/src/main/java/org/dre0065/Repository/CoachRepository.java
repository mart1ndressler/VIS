package org.dre0065.Repository;

import org.dre0065.Model.Coach;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import java.sql.*;
import java.util.*;

@Repository
public class CoachRepository
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Coach> coachRowMapper = new RowMapper<Coach>()
    {
        @Override
        public Coach mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            Coach c = new Coach();
            c.setCoachId(rs.getInt("coach_id"));
            c.setFirstName(rs.getString("first_name"));
            c.setLastName(rs.getString("last_name"));
            c.setSpecialization(rs.getString("specialization"));
            return c;
        }
    };

    public boolean existsByFirstNameAndLastName(String firstName, String lastName)
    {
        String sql = "SELECT COUNT(*) FROM coach WHERE first_name = ? AND last_name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, firstName, lastName);
        return count != null && count > 0;
    }

    public Optional<Coach> findByFirstNameAndLastName(String firstName, String lastName)
    {
        String sql = "SELECT * FROM coach WHERE first_name = ? AND last_name = ?";
        List<Coach> results = jdbcTemplate.query(sql, coachRowMapper, firstName, lastName);
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<Coach> findById(Integer id)
    {
        String sql = "SELECT * FROM coach WHERE coach_id = ?";
        List<Coach> results = jdbcTemplate.query(sql, coachRowMapper, id);
        if (results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public List<Coach> findAll()
    {
        String sql = "SELECT * FROM coach";
        return jdbcTemplate.query(sql, coachRowMapper);
    }

    public Coach save(Coach coach)
    {
        if(coach.getCoachId() == 0)
        {
            String sql = "INSERT INTO coach (first_name, last_name, specialization) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, coach.getFirstName(), coach.getLastName(), coach.getSpecialization());
            Integer newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            if(newId != null) coach.setCoachId(newId);
            return coach;
        }
        else
        {
            String sql = "UPDATE coach SET first_name=?, last_name=?, specialization=? WHERE coach_id=?";
            jdbcTemplate.update(sql, coach.getFirstName(), coach.getLastName(), coach.getSpecialization(), coach.getCoachId());
            return coach;
        }
    }

    public void saveAll(List<Coach> coaches) {for(Coach coach : coaches) save(coach);}

    public void deleteById(Integer id)
    {
        String sql = "DELETE FROM coach WHERE coach_id = ?";
        jdbcTemplate.update(sql, id);
    }
}