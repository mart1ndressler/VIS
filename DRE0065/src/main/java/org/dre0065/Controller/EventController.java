package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.Event;
import org.dre0065.Service.EventService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/events")
public class EventController
{
    @Autowired
    private EventService eventService;

    @PostMapping("/add")
    public String addEvents(@Valid @RequestBody List<Event> events)
    {
        eventService.saveAllEvents(events);
        return "Events added successfully!";
    }

    @GetMapping("/all")
    public List<Event> getAllEvents() {return eventService.getAllEvents();}

    @PostMapping("/load-json")
    public String loadEventsFromJson()
    {
        eventService.loadUniqueEventsFromJson();
        return "Events loaded from JSON successfully!";
    }
}