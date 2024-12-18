package org.dre0065.Repository;

import org.dre0065.Model.MMAFight;
import org.dre0065.Model.Fight;
import org.dre0065.Model.MMAFighter;
import org.springframework.stereotype.*;
import jakarta.persistence.*;
import java.util.*;

@Repository
public class MMAFightRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<MMAFight> findByFightAndFighter(Fight fight, MMAFighter fighter)
    {
        String jpql = "SELECT m FROM MMAFight m WHERE m.fight = :fight AND m.fighter = :fighter";
        List<MMAFight> results = entityManager.createQuery(jpql, MMAFight.class).setParameter("fight", fight).setParameter("fighter", fighter).getResultList();
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<MMAFight> findById(Integer id)
    {
        MMAFight mmaFight = entityManager.find(MMAFight.class, id);
        return mmaFight != null ? Optional.of(mmaFight) : Optional.empty();
    }

    public List<MMAFight> findAll()
    {
        String jpql = "SELECT m FROM MMAFight m";
        return entityManager.createQuery(jpql, MMAFight.class).getResultList();
    }

    public MMAFight save(MMAFight mmaFight)
    {
        if(mmaFight.getMmaFightId() == 0)
        {
            entityManager.persist(mmaFight);
            return mmaFight;
        }
        else return entityManager.merge(mmaFight);
    }

    public void saveAll(List<MMAFight> mmaFights) {for(MMAFight mmaFight : mmaFights) save(mmaFight);}

    public void deleteById(Integer id)
    {
        MMAFight mmaFight = entityManager.find(MMAFight.class, id);
        if(mmaFight != null) entityManager.remove(mmaFight);
    }

    public void delete(MMAFight mmaFight)
    {
        if(mmaFight != null)
        {
            if(!entityManager.contains(mmaFight)) mmaFight = entityManager.merge(mmaFight);
            entityManager.remove(mmaFight);
        }
    }
}