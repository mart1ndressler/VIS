package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.Coach;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.Preparation;
import org.dre0065.Service.CoachService;
import org.dre0065.Service.MMAFighterService;
import org.dre0065.Service.PreparationService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.*;
import java.text.*;
import java.util.*;

@RestController
@RequestMapping("/api/preparations")
public class PreparationController
{
    @Autowired
    private PreparationService preparationService;

    @Autowired
    private MMAFighterService mmaFighterService;

    @Autowired
    private CoachService coachService;

    @PostMapping("/add")
    public String addPreparation(@Valid @RequestBody Preparation preparation)
    {
        preparationService.createPreparation(preparation);
        return "Preparation added successfully!";
    }

    @GetMapping("/all")
    public List<Preparation> getAllPreparations() {return preparationService.getAllPreparations();}

    @PostMapping("/load-json")
    public String loadPreparationsFromJson()
    {
        preparationService.loadPreparationsFromJson();
        return "Preparations loaded from JSON successfully!";
    }

    @DeleteMapping("/delete/{id}")
    public String deletePreparation(@PathVariable int id)
    {
        Preparation preparation = preparationService.getPreparationById(id);
        if(preparation == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Preparation not found with ID: " + id);

        preparationService.deletePreparationById(id);
        return "Preparation with ID " + id + " and all its dependencies deleted successfully!";
    }

    @GetMapping("/{id}")
    public Preparation getPreparationById(@PathVariable int id)
    {
        Preparation preparation = preparationService.getPreparationById(id);
        if(preparation == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Preparation not found with ID: " + id);
        return preparation;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Preparation> updatePreparation(@PathVariable int id, @RequestBody Map<String, Object> updatedPreparationData)
    {
        Preparation existingPreparation = preparationService.getPreparationById(id);
        if(existingPreparation == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try
        {
            String startOfPrepStr = (String) updatedPreparationData.get("start_of_preparation");
            String endOfPrepStr = (String) updatedPreparationData.get("end_of_preparation");
            Date startOfPreparation = sdf.parse(startOfPrepStr);
            Date endOfPreparation = sdf.parse(endOfPrepStr);
            existingPreparation.setStartOfPreparation(startOfPreparation);
            existingPreparation.setEndOfPreparation(endOfPreparation);
        }
        catch(ParseException e) {return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}

        existingPreparation.setMmaClub((String) updatedPreparationData.get("mma_club"));
        existingPreparation.setClubRegion((String) updatedPreparationData.get("club_region"));

        if(updatedPreparationData.get("mma-fighter") != null)
        {
            Map<String, Object> fighterData = (Map<String, Object>) updatedPreparationData.get("mma-fighter");
            Integer fighterId = null;
            Object fighterIdObj = fighterData.get("fighterId");
            if(fighterIdObj instanceof Integer) fighterId = (Integer) fighterIdObj;
            else if(fighterIdObj instanceof String) fighterId = Integer.parseInt((String) fighterIdObj);

            MMAFighter fighter = mmaFighterService.getFighterById(fighterId);
            if(fighter != null) existingPreparation.setFighter(fighter);
            else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MMA Fighter not found with ID: " + fighterId);
        }

        if(updatedPreparationData.get("coach") != null)
        {
            Map<String, Object> coachData = (Map<String, Object>) updatedPreparationData.get("coach");
            Integer coachId = null;
            Object coachIdObj = coachData.get("coachId");
            if(coachIdObj instanceof Integer) coachId = (Integer) coachIdObj;
            else if(coachIdObj instanceof String) coachId = Integer.parseInt((String) coachIdObj);

            Coach coach = coachService.getCoachById(coachId);
            if(coach != null) existingPreparation.setCoach(coach);
            else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach not found with ID: " + coachId);
        }

        preparationService.updatePreparationById(id, existingPreparation);
        return new ResponseEntity<>(existingPreparation, HttpStatus.OK);
    }
}