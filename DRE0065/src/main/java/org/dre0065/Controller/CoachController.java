package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.Coach;
import org.dre0065.Service.CoachService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCoach(@PathVariable int id, @RequestParam(defaultValue = "false") boolean withDependencies)
    {
        if(withDependencies)
        {
            if(coachService.hasDependencies(id))
            {
                coachService.deleteCoachWithDependencies(id);
                return ResponseEntity.ok("Coach and all related records deleted successfully!");
            }
        }
        else if(coachService.hasDependencies(id)) return ResponseEntity.status(HttpStatus.CONFLICT).body("Coach has dependencies in other tables and cannot be deleted.");
        coachService.deleteCoachById(id);
        return ResponseEntity.ok("Coach deleted successfully!");
    }

    @DeleteMapping("/delete-with-dependencies/{id}")
    public ResponseEntity<String> deleteCoachWithDependencies(@PathVariable int id)
    {
        if(coachService.hasDependencies(id))
        {
            coachService.deleteCoachWithDependencies(id);
            return ResponseEntity.ok("Coach and all related records deleted successfully!");
        }
        coachService.deleteCoachById(id);
        return ResponseEntity.ok("Coach deleted successfully!");
    }

    @PutMapping("/update/{id}")
    public String updateCoach(@PathVariable int id, @Valid @RequestBody Coach updatedCoach) {return coachService.updateCoachById(id, updatedCoach);}
}