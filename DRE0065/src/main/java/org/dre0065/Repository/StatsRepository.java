package org.dre0065.Repository;

import org.dre0065.Model.Stats;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.WeightCategory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.*;
import java.util.*;

@Repository
public class StatsRepository
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Stats> statsRowMapper = (rs, rowNum) ->
    {
        Stats s = new Stats();
        s.setStatsId(rs.getInt("stats_id"));
        s.setWins(rs.getInt("wins"));
        s.setLosses(rs.getInt("losses"));
        s.setDraws(rs.getInt("draws"));
        s.setKos(rs.getInt("kos"));
        s.setTkos(rs.getInt("tkos"));
        s.setSubmissions(rs.getInt("submissions"));
        s.setDecisions(rs.getInt("decisions"));

        WeightCategory wc = new WeightCategory();
        wc.setWeightCategoryId(rs.getInt("wc_id"));
        wc.setName(rs.getString("wc_name"));
        wc.setMinWeight(rs.getString("wc_min_weight"));
        wc.setMaxWeight(rs.getString("wc_max_weight"));

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
        f.setWeightCategory(wc);

        s.setFighter(f);
        return s;
    };

    public Optional<Stats> findByFighter(MMAFighter fighter)
    {
        String sql = "SELECT s.stats_id, s.wins, s.losses, s.draws, s.kos, s.tkos, s.submissions, s.decisions, f.fighter_id, f.first_name, f.last_name, f.weight, f.height, f.reach, f.nationality, f.ranking, f.fights, f.points, w.weight_category_id AS wc_id, w.name AS wc_name, w.min_weight AS wc_min_weight, w.max_weight AS wc_max_weight " +
                "FROM stats s JOIN mma_fighter f ON s.fighter_id = f.fighter_id JOIN weight_category w ON f.weight_category_id = w.weight_category_id " +
                "WHERE s.fighter_id = ?";
        List<Stats> results = jdbcTemplate.query(sql, statsRowMapper, fighter.getFighterId());
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<Stats> findById(Integer id)
    {
        String sql = "SELECT s.stats_id, s.wins, s.losses, s.draws, s.kos, s.tkos, s.submissions, s.decisions, f.fighter_id, f.first_name, f.last_name, f.weight, f.height, f.reach, f.nationality, f.ranking, f.fights, f.points, w.weight_category_id AS wc_id, w.name AS wc_name, w.min_weight AS wc_min_weight, w.max_weight AS wc_max_weight " +
                "FROM stats s JOIN mma_fighter f ON s.fighter_id = f.fighter_id JOIN weight_category w ON f.weight_category_id = w.weight_category_id " +
                "WHERE s.stats_id = ?";
        List<Stats> results = jdbcTemplate.query(sql, statsRowMapper, id);
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public List<Stats> findAll()
    {
        String sql = "SELECT s.stats_id, s.wins, s.losses, s.draws, s.kos, s.tkos, s.submissions, s.decisions, f.fighter_id, f.first_name, f.last_name, f.weight, f.height, f.reach, f.nationality, f.ranking, f.fights, f.points, w.weight_category_id AS wc_id, w.name AS wc_name, w.min_weight AS wc_min_weight, w.max_weight AS wc_max_weight " +
                "FROM stats s JOIN mma_fighter f ON s.fighter_id = f.fighter_id JOIN weight_category w ON f.weight_category_id = w.weight_category_id";
        return jdbcTemplate.query(sql, statsRowMapper);
    }

    public Stats save(Stats stats)
    {
        if(stats.getStatsId() == 0)
        {
            String sql = "INSERT INTO stats (wins, losses, draws, kos, tkos, submissions, decisions, fighter_id) VALUES (?,?,?,?,?,?,?,?)";
            jdbcTemplate.update(sql, stats.getWins(), stats.getLosses(), stats.getDraws(), stats.getKos(), stats.getTkos(), stats.getSubmissions(), stats.getDecisions(), stats.getFighter().getFighterId());
            Integer newId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            if(newId != null) stats.setStatsId(newId);
            return stats;
        }
        else
        {
            String sql = "UPDATE stats SET wins=?, losses=?, draws=?, kos=?, tkos=?, submissions=?, decisions=?, fighter_id=? WHERE stats_id=?";
            jdbcTemplate.update(sql, stats.getWins(), stats.getLosses(), stats.getDraws(), stats.getKos(), stats.getTkos(), stats.getSubmissions(), stats.getDecisions(), stats.getFighter().getFighterId(), stats.getStatsId());
            return stats;
        }
    }

    public void saveAll(List<Stats> statsList) {for(Stats s : statsList) save(s);}

    public void deleteById(Integer id)
    {
        String sql = "DELETE FROM stats WHERE stats_id=?";
        jdbcTemplate.update(sql, id);
    }

    public void delete(Stats stats) {deleteById(stats.getStatsId());}

    public void deleteByFighterId(int fighterId)
    {
        String sql = "DELETE FROM stats WHERE fighter_id = ?";
        jdbcTemplate.update(sql, fighterId);
    }

    public void deleteByWeightCategoryId(int weightCategoryId)
    {
        String sql = "DELETE FROM stats WHERE fighter_id IN (SELECT fighter_id FROM mma_fighter WHERE weight_category_id=?)";
        jdbcTemplate.update(sql, weightCategoryId);
    }
}