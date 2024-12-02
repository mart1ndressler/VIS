package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.Coach;
import org.dre0065.Service.CoachService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/coaches")
public class CoachController
{
    @Autowired
    private CoachService coachService;

    @PostMapping("/add")
    public String addCoaches(@Valid @RequestBody List<Coach> coaches)
    {
        System.out.println("Received request to add coaches: " + coaches);
        coachService.saveAllCoaches(coaches);
        return "Coaches added successfully!";
    }

    @GetMapping("/all")
    public List<Coach> getAllCoaches() {return coachService.getAllCoaches();}

    @PostMapping("/load-json")
    public String loadCoachesFromJson()
    {
        coachService.loadUniqueCoachesFromJson();
        return "Coaches loaded from JSON successfully!";
    }
}