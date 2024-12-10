package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.MMAFight;
import org.dre0065.Service.MMAFightService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.*;

import java.util.List;

@RestController
@RequestMapping("/api/mmafights")
public class MMAFightController
{
    @Autowired
    private MMAFightService mmaFightService;

    @PostMapping("/add")
    public String addMMAFights(@Valid @RequestBody List<MMAFight> mmaFights)
    {
        mmaFightService.saveAllMMAFights(mmaFights);
        return "MMAFights added successfully!";
    }

    @GetMapping("/all")
    public List<MMAFight> getAllMMAFights() {return mmaFightService.getAllMMAFights();}

    @PostMapping("/load-json")
    public String loadMMAFightsFromJson()
    {
        mmaFightService.loadMMAFightsFromJson();
        return "MMAFights loaded from JSON successfully!";
    }

    @GetMapping("/{id}")
    public MMAFight getMMAFightById(@PathVariable int id)
    {
        MMAFight mmaFight = mmaFightService.getMMAFightById(id);
        if(mmaFight == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MMAFight not found with ID: " + id);
        return mmaFight;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MMAFight> updateMMAFight(@PathVariable int id, @Valid @RequestBody MMAFight updatedMMAFight)
    {
        MMAFight existingMMAFight = mmaFightService.getMMAFightById(id);
        if(existingMMAFight == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        existingMMAFight.setFight(updatedMMAFight.getFight());
        existingMMAFight.setFighter(updatedMMAFight.getFighter());

        mmaFightService.saveMMAFight(existingMMAFight);
        return new ResponseEntity<>(existingMMAFight, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMMAFight(@PathVariable int id)
    {
        MMAFight mmaFight = mmaFightService.getMMAFightById(id);
        if(mmaFight == null) return new ResponseEntity<>("MMAFight not found with ID: " + id, HttpStatus.NOT_FOUND);

        mmaFightService.deleteMMAFightById(id);
        return new ResponseEntity<>("MMAFight deleted successfully!", HttpStatus.OK);
    }
}