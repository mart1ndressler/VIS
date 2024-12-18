package org.dre0065.Repository;

import org.dre0065.Model.MMAFighter;
import org.springframework.stereotype.*;
import jakarta.persistence.*;
import java.util.*;

@Repository
public class MMAFighterRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public boolean existsByFirstNameAndLastName(String firstName, String lastName)
    {
        String jpql = "SELECT COUNT(m) FROM MMAFighter m WHERE m.firstName = :firstName AND m.lastName = :lastName";
        Long count = entityManager.createQuery(jpql, Long.class).setParameter("firstName", firstName).setParameter("lastName", lastName).getSingleResult();
        return count > 0;
    }

    public Optional<MMAFighter> findByFirstNameAndLastName(String firstName, String lastName)
    {
        String jpql = "SELECT m FROM MMAFighter m WHERE m.firstName = :firstName AND m.lastName = :lastName";
        List<MMAFighter> results = entityManager.createQuery(jpql, MMAFighter.class).setParameter("firstName", firstName).setParameter("lastName", lastName).getResultList();
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<MMAFighter> findById(Integer id)
    {
        MMAFighter fighter = entityManager.find(MMAFighter.class, id);
        return fighter != null ? Optional.of(fighter) : Optional.empty();
    }

    public List<MMAFighter> findAll()
    {
        String jpql = "SELECT m FROM MMAFighter m";
        return entityManager.createQuery(jpql, MMAFighter.class).getResultList();
    }

    public MMAFighter save(MMAFighter fighter)
    {
        if(fighter.getFighterId() == 0)
        {
            entityManager.persist(fighter);
            return fighter;
        }
        else return entityManager.merge(fighter);
    }

    public void saveAll(List<MMAFighter> fighters) {for(MMAFighter fighter : fighters) save(fighter);}

    public void deleteById(Integer id)
    {
        MMAFighter fighter = entityManager.find(MMAFighter.class, id);
        if(fighter != null) entityManager.remove(fighter);
    }

    public void delete(MMAFighter fighter)
    {
        if(fighter != null)
        {
            if(!entityManager.contains(fighter)) fighter = entityManager.merge(fighter);
            entityManager.remove(fighter);
        }
    }
}