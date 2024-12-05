package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.*;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Repository.WeightCategoryRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import java.io.*;
import java.util.*;

@Service
public class WeightCategoryService
{
    @Autowired
    private WeightCategoryRepository weightCategoryRepository;

    @PostConstruct
    public void init() {loadUniqueWeightCategoriesFromJson();}

    public void loadUniqueWeightCategoriesFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ClassPathResource resource = new ClassPathResource("weight_categories.json");
            List<WeightCategory> categoriesFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<WeightCategory>>() {});

            for(WeightCategory categoryFromJson : categoriesFromJson)
            {
                boolean exists = weightCategoryRepository.existsByName(categoryFromJson.getName());

                if(!exists)
                {
                    weightCategoryRepository.save(categoryFromJson);
                    System.out.println("Added new weight category: " + categoryFromJson.getName());
                }
                else System.out.println("Weight category already exists: " + categoryFromJson.getName());
            }
            System.out.println("Weight categories successfully processed from JSON!");
        }
        catch(IOException e) {System.err.println("Error loading weight categories from JSON: " + e.getMessage());}
    }

    public void saveAllWeightCategories(List<WeightCategory> categories) {weightCategoryRepository.saveAll(categories);}
    public List<WeightCategory> getAllWeightCategories() {return weightCategoryRepository.findAll();}
    public String updateWeightCategoryById(int id, WeightCategory updatedCategory)
    {
        Optional<WeightCategory> existingCategory = weightCategoryRepository.findById(id);
        if(existingCategory.isPresent())
        {
            WeightCategory category = existingCategory.get();
            category.setName(updatedCategory.getName());
            category.setMinWeight(updatedCategory.getMinWeight());
            category.setMaxWeight(updatedCategory.getMaxWeight());
            weightCategoryRepository.save(category);
            return "Category updated successfully!";
        }
        else throw new RuntimeException("Category with ID " + id + " not found!");
    }

    public void deleteWeightCategoryById(int id)
    {
        if(weightCategoryRepository.existsById(id)) weightCategoryRepository.deleteById(id);
        else throw new RuntimeException("Category with ID " + id + " not found!");
    }
    public WeightCategory getWeightCategoryById(int id) {return weightCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Weight category with ID " + id + " not found."));}
    public WeightCategory getWeightCategoryByName(String name) {return weightCategoryRepository.findByName(name).orElse(null);}
}