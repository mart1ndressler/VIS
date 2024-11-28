package org.dre0065.Controller;

import org.dre0065.Model.Coach;
import org.dre0065.Service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {

    @Autowired
    private CoachService coachService;

    @PostMapping("/add")
    public String addCoaches(@RequestBody List<Coach> coaches) {
        coachService.saveAllCoaches(coaches);
        return "Coaches added successfully!";
    }

    @GetMapping("/all")
    public List<Coach> getAllCoaches() {
        return coachService.getAllCoaches();
    }
}
