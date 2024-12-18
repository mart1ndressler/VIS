package org.dre0065.Repository;

import org.dre0065.Model.Coach;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.Preparation;
import org.springframework.stereotype.*;
import jakarta.persistence.*;
import java.util.*;

@Repository
public class PreparationRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public Preparation findByStartOfPreparationAndEndOfPreparationAndFighterAndCoach(Date startOfPreparation, Date endOfPreparation, MMAFighter fighter, Coach coach)
    {
        String jpql = "SELECT p FROM Preparation p WHERE p.startOfPreparation = :startOfPreparation AND p.endOfPreparation = :endOfPreparation AND p.fighter = :fighter AND p.coach = :coach";
        List<Preparation> results = entityManager.createQuery(jpql, Preparation.class).setParameter("startOfPreparation", startOfPreparation).setParameter("endOfPreparation", endOfPreparation).setParameter("fighter", fighter).setParameter("coach", coach).getResultList();
        if(results.isEmpty()) return null;
        return results.get(0);
    }

    public boolean existsByCoach(Coach coach)
    {
        String jpql = "SELECT COUNT(p) FROM Preparation p WHERE p.coach = :coach";
        Long count = entityManager.createQuery(jpql, Long.class).setParameter("coach", coach).getSingleResult();
        return count > 0;
    }

    public void deleteByCoach(Coach coach)
    {
        String jpql = "DELETE FROM Preparation p WHERE p.coach = :coach";
        entityManager.createQuery(jpql).setParameter("coach", coach).executeUpdate();
    }

    public Optional<Preparation> findById(Integer id)
    {
        Preparation preparation = entityManager.find(Preparation.class, id);
        return preparation != null ? Optional.of(preparation) : Optional.empty();
    }

    public List<Preparation> findAll()
    {
        String jpql = "SELECT p FROM Preparation p";
        return entityManager.createQuery(jpql, Preparation.class).getResultList();
    }

    public Preparation save(Preparation preparation)
    {
        if(preparation.getPreparationId() == 0)
        {
            entityManager.persist(preparation);
            return preparation;
        }
        else return entityManager.merge(preparation);
    }

    public void saveAll(List<Preparation> preparations) {for(Preparation preparation : preparations) save(preparation);}

    public void deleteById(Integer id)
    {
        Preparation preparation = entityManager.find(Preparation.class, id);
        if(preparation != null) entityManager.remove(preparation);
    }

    public void delete(Preparation preparation)
    {
        if(preparation != null)
        {
            if(!entityManager.contains(preparation)) preparation = entityManager.merge(preparation);
            entityManager.remove(preparation);
        }
    }
}