package org.dre0065.Repository;

import org.dre0065.Model.Event;
import org.springframework.stereotype.*;
import jakarta.persistence.*;
import java.util.*;

@Repository
public class EventRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public boolean existsByEventNameAndStartOfEvent(String eventName, Date startOfEvent)
    {
        String jpql = "SELECT COUNT(e) FROM Event e WHERE e.eventName = :eventName AND e.startOfEvent = :startOfEvent";
        Long count = entityManager.createQuery(jpql, Long.class).setParameter("eventName", eventName).setParameter("startOfEvent", startOfEvent).getSingleResult();
        return count > 0;
    }

    public List<Event> findByEventName(String eventName)
    {
        String jpql = "SELECT e FROM Event e WHERE e.eventName = :eventName";
        return entityManager.createQuery(jpql, Event.class).setParameter("eventName", eventName).getResultList();
    }

    public Optional<Event> findById(Integer id)
    {
        Event event = entityManager.find(Event.class, id);
        return event != null ? Optional.of(event) : Optional.empty();
    }

    public List<Event> findAll()
    {
        String jpql = "SELECT e FROM Event e";
        return entityManager.createQuery(jpql, Event.class).getResultList();
    }

    public Event save(Event event)
    {
        if(event.getEventId() == 0)
        {
            entityManager.persist(event);
            return event;
        }
        else return entityManager.merge(event);
    }

    public void saveAll(List<Event> events) {for(Event event : events) save(event);}

    public void deleteById(Integer id)
    {
        Event event = entityManager.find(Event.class, id);
        if(event != null) entityManager.remove(event);
    }
}