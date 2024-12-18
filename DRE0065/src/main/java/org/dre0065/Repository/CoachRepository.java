package org.dre0065.Repository;

import org.dre0065.Model.Coach;
import org.springframework.stereotype.*;
import jakarta.persistence.*;
import java.util.*;

@Repository
public class CoachRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public boolean existsByFirstNameAndLastName(String firstName, String lastName)
    {
        String jpql = "SELECT COUNT(c) FROM Coach c WHERE c.firstName = :firstName AND c.lastName = :lastName";
        Long count = entityManager.createQuery(jpql, Long.class).setParameter("firstName", firstName).setParameter("lastName", lastName).getSingleResult();
        return count > 0;
    }

    public Optional<Coach> findByFirstNameAndLastName(String firstName, String lastName)
    {
        String jpql = "SELECT c FROM Coach c WHERE c.firstName = :firstName AND c.lastName = :lastName";
        List<Coach> results = entityManager.createQuery(jpql, Coach.class).setParameter("firstName", firstName).setParameter("lastName", lastName).getResultList();
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<Coach> findById(Integer id)
    {
        Coach coach = entityManager.find(Coach.class, id);
        return coach != null ? Optional.of(coach) : Optional.empty();
    }

    public List<Coach> findAll()
    {
        String jpql = "SELECT c FROM Coach c";
        return entityManager.createQuery(jpql, Coach.class).getResultList();
    }

    public Coach save(Coach coach)
    {
        if(coach.getCoachId() == 0)
        {
            entityManager.persist(coach);
            return coach;
        }
        else return entityManager.merge(coach);
    }

    public void saveAll(List<Coach> coaches) {for(Coach coach : coaches) save(coach);}

    public void deleteById(Integer id)
    {
        Coach coach = entityManager.find(Coach.class, id);
        if(coach != null) entityManager.remove(coach);
    }
}