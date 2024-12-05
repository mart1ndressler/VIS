package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Service.WeightCategoryService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/weight_categories")
public class WeightCategoryController
{
    @Autowired
    private WeightCategoryService weightCategoryService;

    @PostMapping("/add")
    public String addWeightCategories(@Valid @RequestBody List<WeightCategory> categories)
    {
        weightCategoryService.saveAllWeightCategories(categories);
        return "Weight categories added successfully!";
    }

    @GetMapping("/all")
    public List<WeightCategory> getAllWeightCategories() {return weightCategoryService.getAllWeightCategories();}

    @PostMapping("/load-json")
    public String loadWeightCategoriesFromJson()
    {
        weightCategoryService.loadUniqueWeightCategoriesFromJson();
        return "Weight categories loaded from JSON successfully!";
    }

    @PutMapping("/update/{id}")
    public String updateWeightCategory(@PathVariable int id, @Valid @RequestBody WeightCategory updatedCategory) {return weightCategoryService.updateWeightCategoryById(id, updatedCategory);}

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteWeightCategory(@PathVariable int id)
    {
        weightCategoryService.deleteWeightCategoryById(id);
        return ResponseEntity.ok("Category deleted successfully!");
    }
}