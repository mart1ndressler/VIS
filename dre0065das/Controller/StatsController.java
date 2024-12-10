package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.Stats;
import org.dre0065.Service.StatsService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/stats")
public class StatsController
{
    @Autowired
    private StatsService statsService;

    @PostMapping("/add")
    public String addStats(@Valid @RequestBody List<Stats> stats)
    {
        statsService.saveAllStats(stats);
        return "Stats added successfully!";
    }

    @GetMapping("/all")
    public List<Stats> getAllStats() {return statsService.getAllStats();}

    @PostMapping("/load-json")
    public String loadStatsFromJson()
    {
        statsService.loadStatsFromJson();
        return "Stats loaded from JSON successfully!";
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateStat(@PathVariable int id, @Valid @RequestBody Stats updatedStat)
    {
        boolean isUpdated = statsService.updateStat(id, updatedStat);
        if(isUpdated) return ResponseEntity.ok("Stat updated successfully!");
        else return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStat(@PathVariable int id)
    {
        boolean isDeleted = statsService.deleteStat(id);
        if(isDeleted) return ResponseEntity.ok("Stat deleted successfully!");
        else return ResponseEntity.notFound().build();
    }
}