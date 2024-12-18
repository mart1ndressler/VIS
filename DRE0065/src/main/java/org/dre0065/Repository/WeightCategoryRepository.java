package org.dre0065.Repository;

import org.dre0065.Model.WeightCategory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import java.sql.*;
import java.util.*;

@Repository
public class WeightCategoryRepository
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<WeightCategory> categoryRowMapper = new RowMapper<WeightCategory>()
    {
        @Override
        public WeightCategory mapRow(ResultSet rs, int rowNum) throws SQLException
        {
            WeightCategory w = new WeightCategory();
            w.setWeightCategoryId(rs.getInt("weight_category_id"));
            w.setName(rs.getString("name"));
            w.setMinWeight(rs.getString("min_weight"));
            w.setMaxWeight(rs.getString("max_weight"));
            return w;
        }
    };

    public boolean existsByName(String name)
    {
        String sql = "SELECT COUNT(*) FROM weight_category WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    public Optional<WeightCategory> findByName(String name)
    {
        String sql = "SELECT * FROM weight_category WHERE name = ?";
        List<WeightCategory> results = jdbcTemplate.query(sql, categoryRowMapper, name);
        if (results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<WeightCategory> findById(Integer id)
    {
        String sql = "SELECT * FROM weight_category WHERE weight_category_id = ?";
        List<WeightCategory> results = jdbcTemplate.query(sql, categoryRowMapper, id);
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public List<WeightCategory> findAll()
    {
        String sql = "SELECT * FROM weight_category";
        return jdbcTemplate.query(sql, categoryRowMapper);
    }

    public WeightCategory save(WeightCategory category)
    {
        if(category.getWeightCategoryId() == null)
        {
            String sql = "INSERT INTO weight_category (name, min_weight, max_weight) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, category.getName(), category.getMinWeight(), category.getMaxWeight());
            Integer newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            if(newId != null) category.setWeightCategoryId(newId);
            return category;
        }
        else
        {
            String sql = "UPDATE weight_category SET name=?, min_weight=?, max_weight=? WHERE weight_category_id=?";
            jdbcTemplate.update(sql, category.getName(), category.getMinWeight(), category.getMaxWeight(), category.getWeightCategoryId());
            return category;
        }
    }

    public void saveAll(List<WeightCategory> categories) {for(WeightCategory category : categories) save(category);}

    public void deleteById(Integer id)
    {
        String sql = "DELETE FROM weight_category WHERE weight_category_id = ?";
        jdbcTemplate.update(sql, id);
    }
}