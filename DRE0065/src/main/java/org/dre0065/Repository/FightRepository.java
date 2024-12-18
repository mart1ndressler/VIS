package org.dre0065.Repository;

import org.dre0065.Model.Event;
import org.dre0065.Model.Fight;
import org.dre0065.Model.WeightCategory;
import org.springframework.stereotype.*;
import jakarta.persistence.*;
import java.util.*;

@Repository
public class FightRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Fight> findByDateAndWeightCategoryAndEvent(Date date, WeightCategory weightCategory, Event event)
    {
        String jpql = "SELECT f FROM Fight f WHERE f.date = :date AND f.weightCategory = :weightCategory AND f.event = :event";
        List<Fight> results = entityManager.createQuery(jpql, Fight.class).setParameter("date", date).setParameter("weightCategory", weightCategory).setParameter("event", event).getResultList();
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<Fight> findById(Integer id)
    {
        Fight fight = entityManager.find(Fight.class, id);
        return fight != null ? Optional.of(fight) : Optional.empty();
    }

    public List<Fight> findAll()
    {
        String jpql = "SELECT f FROM Fight f";
        return entityManager.createQuery(jpql, Fight.class).getResultList();
    }

    public Fight save(Fight fight)
    {
        if(fight.getFightId() == 0)
        {
            entityManager.persist(fight);
            return fight;
        }
        else return entityManager.merge(fight);
    }

    public void saveAll(List<Fight> fights) {for(Fight fight : fights) save(fight);}

    public void deleteById(Integer id)
    {
        Fight fight = entityManager.find(Fight.class, id);
        if(fight != null) entityManager.remove(fight);
    }

    public void delete(Fight fight)
    {
        if(fight != null)
        {
            if(!entityManager.contains(fight)) fight = entityManager.merge(fight);
            entityManager.remove(fight);
        }
    }
}