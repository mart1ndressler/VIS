package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Service.MMAFighterService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/mma-fighters")
public class MMAFighterController
{
    @Autowired
    private MMAFighterService mmaFighterService;

    @PostMapping("/add")
    public String addMMAFighters(@Valid @RequestBody List<MMAFighter> fighters)
    {
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
}