package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Service.MMAFighterService;
import org.dre0065.Service.WeightCategoryService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.web.server.*;

@RestController
@RequestMapping("/api/mma-fighters")
public class MMAFighterController
{
    @Autowired
    private MMAFighterService mmaFighterService;

    @Autowired
    private WeightCategoryService weightCategoryService;

    @PostMapping("/add")
    public String addMMAFighters(@Valid @RequestBody List<MMAFighter> fighters)
    {
        for(MMAFighter fighter : fighters)
        {
            if(fighter.getWeightCategory() == null || fighter.getWeightCategory().getName() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Weight category is required and must have a valid name.");
            WeightCategory category = weightCategoryService.getWeightCategoryByName(fighter.getWeightCategory().getName());

            if(category == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid weight category name: " + fighter.getWeightCategory().getName());
            fighter.setWeightCategory(category);
        }
        mmaFighterService.saveAllFighters(fighters);
        return "Fighters added successfully!";
    }

    @GetMapping("/all")
    public List<MMAFighter> getAllMMAFighters() {return mmaFighterService.getAllFighters();}

    @PostMapping("/load-json")
    public String loadMMAFightersFromJson()
    {
        mmaFighterService.loadUniqueFightersFromJson();
        return "MMA Fighters loaded from JSON successfully!";
    }

    @GetMapping("/unique-nationalities")
    public Set<String> getUniqueNationalities() {return mmaFighterService.getUniqueNationalities();}

    @DeleteMapping("/delete/{id}")
    public String deleteMMAFighter(@PathVariable int id)
    {
        mmaFighterService.deleteFighterById(id);
        return "Fighter with ID " + id + " and all its dependencies deleted successfully!";
    }

    @PutMapping("/update/{id}")
    public String updateMMAFighter(@PathVariable int id, @RequestBody Map<String, Object> updatedFighterData)
    {
        MMAFighter existingFighter = mmaFighterService.getFighterById(id);
        if(existingFighter == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fighter not found with ID: " + id);

        existingFighter.setFirstName((String) updatedFighterData.get("first_name"));
        existingFighter.setLastName((String) updatedFighterData.get("last_name"));
        existingFighter.setWeight((String) updatedFighterData.get("weight"));
        existingFighter.setHeight((String) updatedFighterData.get("height"));
        existingFighter.setReach((String) updatedFighterData.get("reach"));
        existingFighter.setNationality((String) updatedFighterData.get("nationality"));
        existingFighter.setRanking((String) updatedFighterData.get("ranking"));
        existingFighter.setFights((Integer) updatedFighterData.get("fights"));
        existingFighter.setPoints((Integer) updatedFighterData.get("points"));

        Object weightCategoryDataObj = updatedFighterData.get("weight_category_id");
        if(weightCategoryDataObj instanceof Map)
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> weightCategoryData = (Map<String, Object>) weightCategoryDataObj;
            Integer weightCategoryId = (Integer) weightCategoryData.get("weightCategoryId");
            WeightCategory category = weightCategoryService.getWeightCategoryById(weightCategoryId);
            if(category != null) existingFighter.setWeightCategory(category);
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid weight category ID: " + weightCategoryId);
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid weight category data format");
        return mmaFighterService.updateFighterById(id, existingFighter);
    }

    @GetMapping("/{id}")
    public MMAFighter getFighterById(@PathVariable int id)
    {
        MMAFighter fighter = mmaFighterService.getFighterById(id);
        if(fighter == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fighter not found with ID: " + id);
        return fighter;
    }
}