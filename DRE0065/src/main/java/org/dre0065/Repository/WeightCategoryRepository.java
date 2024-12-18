package org.dre0065.Repository;

import org.dre0065.Model.WeightCategory;
import org.springframework.stereotype.*;
import jakarta.persistence.*;
import java.util.*;

@Repository
public class WeightCategoryRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public boolean existsByName(String name)
    {
        String jpql = "SELECT COUNT(w) FROM WeightCategory w WHERE w.name = :name";
        Long count = entityManager.createQuery(jpql, Long.class).setParameter("name", name).getSingleResult();
        return count > 0;
    }

    public Optional<WeightCategory> findByName(String name)
    {
        String jpql = "SELECT w FROM WeightCategory w WHERE w.name = :name";
        List<WeightCategory> results = entityManager.createQuery(jpql, WeightCategory.class).setParameter("name", name).getResultList();
        if(results.isEmpty()) return Optional.empty();
        return Optional.of(results.get(0));
    }

    public Optional<WeightCategory> findById(Integer id)
    {
        WeightCategory category = entityManager.find(WeightCategory.class, id);
        return category != null ? Optional.of(category) : Optional.empty();
    }

    public List<WeightCategory> findAll()
    {
        String jpql = "SELECT w FROM WeightCategory w";
        return entityManager.createQuery(jpql, WeightCategory.class).getResultList();
    }

    public WeightCategory save(WeightCategory category)
    {
        if(category.getWeightCategoryId() == null)
        {
            entityManager.persist(category);
            return category;
        }
        else return entityManager.merge(category);
    }

    public void saveAll(List<WeightCategory> categories)
    {
        for(WeightCategory category : categories) save(category);
    }

    public void deleteById(Integer id)
    {
        WeightCategory category = entityManager.find(WeightCategory.class, id);
        if(category != null) entityManager.remove(category);
    }
}