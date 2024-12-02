package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.Preparation;
import org.dre0065.Service.PreparationService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/preparations")
public class PreparationController
{
    @Autowired
    private PreparationService preparationService;

    @PostMapping("/add")
    public String addPreparations(@Valid @RequestBody List<Preparation> preparations)
    {
        preparationService.saveAllPreparations(preparations);
        return "Preparations added successfully!";
    }

    @GetMapping("/all")
    public List<Preparation> getAllPreparations() {return preparationService.getAllPreparations();}

    @PostMapping("/load-json")
    public String loadPreparationsFromJson()
    {
        preparationService.loadPreparationsFromJson();
        return "Preparations loaded from JSON successfully!";
    }
}