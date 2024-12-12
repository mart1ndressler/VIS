package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.Fight;
import org.dre0065.Service.FightService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.*;
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
    public List<Fight> getAllFights() {return fightService.getAllFights();}

    @PostMapping("/load-json")
    public String loadFightsFromJson()
    {
        fightService.loadFightsFromJson();
        return "Fights loaded from JSON successfully!";
    }

    @GetMapping("/{id}")
    public Fight getFightById(@PathVariable int id)
    {
        Fight fight = fightService.getFightById(id);
        if(fight == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fight not found with ID: " + id);
        return fight;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Fight> updateFight(@PathVariable int id, @Valid @RequestBody Fight updatedFight)
    {
        Fight existingFight = fightService.getFightById(id);
        if(existingFight == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        existingFight.setDate(updatedFight.getDate());
        existingFight.setResult(updatedFight.getResult());
        existingFight.setTypeOfResult(updatedFight.getTypeOfResult());
        if(updatedFight.getWeightCategory() != null) existingFight.setWeightCategory(updatedFight.getWeightCategory());
        if(updatedFight.getEvent() != null) existingFight.setEvent(updatedFight.getEvent());

        fightService.saveFight(existingFight);
        return new ResponseEntity<>(existingFight, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFight(@PathVariable int id)
    {
        Fight fight = fightService.getFightById(id);
        if(fight == null) return new ResponseEntity<>("Fight not found with ID: " + id, HttpStatus.NOT_FOUND);

        fightService.deleteFightById(id);
        return new ResponseEntity<>("Fight deleted successfully!", HttpStatus.OK);
    }
}