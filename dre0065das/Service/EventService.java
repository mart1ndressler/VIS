package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.*;
import org.dre0065.Model.Event;
import org.dre0065.Repository.EventRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import java.io.*;
import java.text.*;
import java.util.*;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @PostConstruct
    public void init() {
        loadUniqueEventsFromJson();
    }

    public void loadUniqueEventsFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        try {
            ClassPathResource resource = new ClassPathResource("events.json");
            List<Event> eventsFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Event>>() {});

            for (Event eventFromJson : eventsFromJson) {
                boolean exists = eventRepository.existsByEventNameAndStartOfEvent(eventFromJson.getEventName(), eventFromJson.getStartOfEvent());
                if (!exists) {
                    // Použití Factory Patternu při vytváření instance Event
                    Event eventToSave = Event.createEvent(
                            eventFromJson.getEventName(),
                            eventFromJson.getMmaOrganization(),
                            eventFromJson.getStartOfEvent(),
                            eventFromJson.getEndOfEvent(),
                            eventFromJson.getLocation()
                    );

                    eventRepository.save(eventToSave);
                    System.out.println("Added new event: " + eventToSave.getEventName() + " on " + eventToSave.getStartOfEvent());
                } else {
                    System.out.println("Event already exists: " + eventFromJson.getEventName() + " on " + eventFromJson.getStartOfEvent());
                }
            }
            System.out.println("Events successfully processed from JSON!");
        } catch (IOException e) {
            System.err.println("Error loading events from JSON: " + e.getMessage());
        }
    }

    public void saveAllEvents(List<Event> events) {
        // Použití Factory Patternu pro každého eventa před uložením
        List<Event> eventsToSave = new ArrayList<>();
        for (Event event : events) {
            Event eventBuilt = Event.createEvent(
                    event.getEventName(),
                    event.getMmaOrganization(),
                    event.getStartOfEvent(),
                    event.getEndOfEvent(),
                    event.getLocation()
            );
            eventsToSave.add(eventBuilt);
        }
        eventRepository.saveAll(eventsToSave);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventByName(String eventName) {
        List<Event> events = eventRepository.findByEventName(eventName);
        if (events.isEmpty()) {
            System.out.println("No event found with name: " + eventName);
            return null;
        }
        System.out.println(events.size() > 1
                ? "Multiple events found with name: " + eventName + ". Returning the first one."
                : "Event found with name: " + eventName);
        return events.get(0);
    }

    public String updateEventById(int id, Event updatedEvent) {
        Optional<Event> existingEventOpt = eventRepository.findById(id);
        if (existingEventOpt.isPresent()) {
            Event existingEvent = existingEventOpt.get();

            // Aktualizace atributů pomocí setterů
            existingEvent.setEventName(updatedEvent.getEventName());
            existingEvent.setMmaOrganization(updatedEvent.getMmaOrganization());
            existingEvent.setStartOfEvent(updatedEvent.getStartOfEvent());
            existingEvent.setEndOfEvent(updatedEvent.getEndOfEvent());
            existingEvent.setLocation(updatedEvent.getLocation());

            eventRepository.save(existingEvent);
            return "Event updated successfully!";
        } else {
            return "Event with ID " + id + " not found!";
        }
    }

    public void deleteEventById(int id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
        } else {
            throw new RuntimeException("Event with ID " + id + " not found!");
        }
    }
}
