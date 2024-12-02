package org.dre0065.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.dre0065.Model.Event;
import org.dre0065.Repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.text.*;
import java.util.*;

@Service
public class EventService
{
    @Autowired
    private EventRepository eventRepository;

    @PostConstruct
    public void init() {loadUniqueEventsFromJson();}

    public void loadUniqueEventsFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        try
        {
            ClassPathResource resource = new ClassPathResource("events.json");
            List<Event> eventsFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Event>>() {});

            for(Event eventFromJson : eventsFromJson)
            {
                boolean exists = eventRepository.existsByEventNameAndStartOfEvent(eventFromJson.getEventName(), eventFromJson.getStartOfEvent());
                if(!exists)
                {
                    eventRepository.save(eventFromJson);
                    System.out.println("Added new event: " + eventFromJson.getEventName() + " on " + eventFromJson.getStartOfEvent());
                }
                else System.out.println("Event already exists: " + eventFromJson.getEventName() + " on " + eventFromJson.getStartOfEvent());
            }
            System.out.println("Events successfully processed from JSON!");
        }
        catch(IOException e) {System.err.println("Error loading events from JSON: " + e.getMessage());}
    }

    public void saveAllEvents(List<Event> events) {eventRepository.saveAll(events);}
    public List<Event> getAllEvents() {return eventRepository.findAll();}
    public Event getEventByName(String eventName)
    {
        List<Event> events = eventRepository.findByEventName(eventName);
        if(events.isEmpty()) {System.out.println("No event found with name: " + eventName); return null;}
        System.out.println(events.size() > 1
                ? "Multiple events found with name: " + eventName + ". Returning the first one."
                : "Event found with name: " + eventName);
        return events.get(0);
    }
}