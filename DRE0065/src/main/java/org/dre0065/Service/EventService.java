package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.*;
import org.dre0065.Model.Event;
import org.dre0065.Repository.EventRepository;
import org.dre0065.Event.EntityAddedEvent;
import org.dre0065.Event.EntityOperationType;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import java.io.*;
import java.util.*;

@Service
public class EventService
{
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void init() {loadUniqueEventsFromJson();}

    public void loadUniqueEventsFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        try
        {
            ClassPathResource resource = new ClassPathResource("events.json");
            List<Event> eventsFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Event>>() {});

            for(Event eventFromJson : eventsFromJson)
            {
                boolean exists = eventRepository.existsByEventNameAndStartOfEvent(eventFromJson.getEventName(), eventFromJson.getStartOfEvent());
                if(!exists)
                {
                    Event eventToSave = Event.createEvent(eventFromJson.getEventName(), eventFromJson.getMmaOrganization(), eventFromJson.getStartOfEvent(), eventFromJson.getEndOfEvent(), eventFromJson.getLocation());
                    eventRepository.save(eventToSave);
                    eventPublisher.publishEvent(new EntityAddedEvent(this, eventToSave, EntityOperationType.CREATE));
                }
            }
        }
        catch(IOException e) {System.err.println("Error loading events from JSON: " + e.getMessage());}
    }

    public void saveAllEvents(List<Event> events)
    {
        List<Event> eventsToSave = new ArrayList<>();
        for(Event event : events)
        {
            Event eventBuilt = Event.createEvent(event.getEventName(), event.getMmaOrganization(), event.getStartOfEvent(), event.getEndOfEvent(), event.getLocation());
            eventsToSave.add(eventBuilt);
        }
        eventRepository.saveAll(eventsToSave);
        for(Event savedEvent : eventsToSave) eventPublisher.publishEvent(new EntityAddedEvent(this, savedEvent, EntityOperationType.CREATE));
    }

    public List<Event> getAllEvents() {return eventRepository.findAll();}

    public Event getEventByName(String eventName)
    {
        List<Event> events = eventRepository.findByEventName(eventName);
        if(events.isEmpty()) return null;
        return events.get(0);
    }

    public String updateEventById(int id, Event updatedEvent)
    {
        Optional<Event> existingEventOpt = eventRepository.findById(id);
        if(existingEventOpt.isPresent())
        {
            Event existingEvent = existingEventOpt.get();
            existingEvent.setEventName(updatedEvent.getEventName());
            existingEvent.setMmaOrganization(updatedEvent.getMmaOrganization());
            existingEvent.setStartOfEvent(updatedEvent.getStartOfEvent());
            existingEvent.setEndOfEvent(updatedEvent.getEndOfEvent());
            existingEvent.setLocation(updatedEvent.getLocation());
            eventRepository.save(existingEvent);
            eventPublisher.publishEvent(new EntityAddedEvent(this, existingEvent, EntityOperationType.UPDATE));
            return "Event updated successfully!";
        }
        else return "Event with ID " + id + " not found!";
    }

    public void deleteEventById(int id)
    {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if(eventOpt.isPresent())
        {
            Event event = eventOpt.get();
            eventRepository.deleteById(id);
            eventPublisher.publishEvent(new EntityAddedEvent(this, event, EntityOperationType.DELETE));
        }
        else throw new RuntimeException("Event with ID " + id + " not found!");
    }
}