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
    public ResponseEntity<String> addStats(@Valid @RequestBody Stats stats)
    {
        try
        {
            statsService.createStats(stats);
            return new ResponseEntity<>("Stats added successfully!", HttpStatus.CREATED);
        }
        catch(IllegalStateException e) {return new ResponseEntity<>("This fighter already has stats.", HttpStatus.CONFLICT);}
        catch(IllegalArgumentException e) {return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);}
        catch(Exception e) {return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);}
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
    public ResponseEntity<?> updateStats(@PathVariable int id, @RequestBody Stats updatedStat)
    {
        try
        {
            statsService.updateStatById(id, updatedStat);
            Stats stat = statsService.getStatsById(id);
            return new ResponseEntity<>(stat, HttpStatus.OK);
        }
        catch(IllegalStateException e) {return new ResponseEntity<>("This fighter already has stats.", HttpStatus.CONFLICT);}
        catch(IllegalArgumentException e) {return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);}
        catch(Exception e) {return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStats(@PathVariable int id)
    {
        try
        {
            statsService.deleteStatById(id);
            return new ResponseEntity<>("Stats with ID " + id + " deleted successfully!", HttpStatus.OK);
        }
        catch(IllegalArgumentException e) {return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);}
        catch(Exception e) {return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);}
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stats> getStatsById(@PathVariable int id)
    {
        Stats stat = statsService.getStatsById(id);
        if(stat == null) {return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);}
        return new ResponseEntity<>(stat, HttpStatus.OK);
    }
}