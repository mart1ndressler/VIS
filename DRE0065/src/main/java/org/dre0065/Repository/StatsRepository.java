package org.dre0065.Repository;

import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.Stats;
import org.springframework.stereotype.*;
import jakarta.persistence.*;
import java.util.*;

@Repository
public class StatsRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Stats> findByFighter(MMAFighter fighter)
    {
        String jpql = "SELECT s FROM Stats s WHERE s.fighter = :fighter";
        List<Stats> results = entityManager.createQuery(jpql, Stats.class).setParameter("fighter", fighter).getResultList();
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<Stats> findById(Integer id)
    {
        Stats stats = entityManager.find(Stats.class, id);
        return stats != null ? Optional.of(stats) : Optional.empty();
    }

    public List<Stats> findAll()
    {
        String jpql = "SELECT s FROM Stats s";
        return entityManager.createQuery(jpql, Stats.class).getResultList();
    }

    public Stats save(Stats stats)
    {
        if(stats.getStatsId() == 0)
        {
            entityManager.persist(stats);
            return stats;
        }
        else return entityManager.merge(stats);
    }

    public void saveAll(List<Stats> statsList)
    {
        for(Stats stats : statsList) {save(stats);}
    }

    public void deleteById(Integer id)
    {
        Stats stats = entityManager.find(Stats.class, id);
        if(stats != null) entityManager.remove(stats);
    }

    public void delete(Stats stats)
    {
        if(stats != null)
        {
            if(!entityManager.contains(stats)) stats = entityManager.merge(stats);
            entityManager.remove(stats);
        }
    }
}