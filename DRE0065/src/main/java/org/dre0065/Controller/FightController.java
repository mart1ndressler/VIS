package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.Fight;
import org.dre0065.Service.FightService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/fights")
public class FightController
{
    @Autowired
    private FightService fightService;

    @PostMapping("/add")
    public String addFights(@Valid @RequestBody List<Fight> fights)
    {
        fightService.saveAllFights(fights);
        return "Fights added successfully!";
    }

    @GetMapping("/all")
    public List<Fight> getAllFights() { return fightService.getAllFights(); }

    @PostMapping("/load-json")
    public String loadFightsFromJson()
    {
        fightService.loadFightsFromJson();
        return "Fights loaded from JSON successfully!";
    }
}