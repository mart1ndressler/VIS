package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.*;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Repository.WeightCategoryRepository;
import org.dre0065.Event.EntityAddedEvent;
import org.dre0065.Event.EntityOperationType;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import java.io.*;
import java.util.*;

@Service
public class WeightCategoryService
{
    @Autowired
    private WeightCategoryRepository weightCategoryRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
                    WeightCategory categoryToSave = WeightCategory.createWeightCategory(categoryFromJson.getName(), categoryFromJson.getMinWeight(), categoryFromJson.getMaxWeight());
                    weightCategoryRepository.save(categoryToSave);
                    eventPublisher.publishEvent(new EntityAddedEvent(this, categoryToSave, EntityOperationType.CREATE));
                }
            }
        }
        catch(IOException e) {System.err.println("Error loading weight categories from JSON: " + e.getMessage());}
    }

    public void saveAllWeightCategories(List<WeightCategory> categories)
    {
        List<WeightCategory> categoriesToSave = new ArrayList<>();
        for(WeightCategory category : categories)
        {
            WeightCategory categoryBuilt = WeightCategory.createWeightCategory(category.getName(), category.getMinWeight(), category.getMaxWeight());
            categoriesToSave.add(categoryBuilt);
        }
        weightCategoryRepository.saveAll(categoriesToSave);
        for(WeightCategory savedCategory : categoriesToSave) eventPublisher.publishEvent(new EntityAddedEvent(this, savedCategory, EntityOperationType.CREATE));
    }

    public List<WeightCategory> getAllWeightCategories() {return weightCategoryRepository.findAll();}
    public WeightCategory getWeightCategoryById(int id) {return weightCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Weight category with ID " + id + " not found."));}
    public WeightCategory getWeightCategoryByName(String name) {return weightCategoryRepository.findByName(name).orElse(null);}

    public String updateWeightCategoryById(int id, WeightCategory updatedCategory)
    {
        Optional<WeightCategory> existingCategoryOpt = weightCategoryRepository.findById(id);
        if(existingCategoryOpt.isPresent())
        {
            WeightCategory existingCategory = existingCategoryOpt.get();
            existingCategory.setName(updatedCategory.getName());
            existingCategory.setMinWeight(updatedCategory.getMinWeight());
            existingCategory.setMaxWeight(updatedCategory.getMaxWeight());
            weightCategoryRepository.save(existingCategory);
            eventPublisher.publishEvent(new EntityAddedEvent(this, existingCategory, EntityOperationType.UPDATE));
            return "Category updated successfully!";
        }
        else throw new RuntimeException("Category with ID " + id + " not found!");
    }

    public void deleteWeightCategoryById(int id)
    {
        Optional<WeightCategory> categoryOpt = weightCategoryRepository.findById(id);
        if(categoryOpt.isPresent())
        {
            WeightCategory category = categoryOpt.get();
            weightCategoryRepository.deleteById(id);
            eventPublisher.publishEvent(new EntityAddedEvent(this, category, EntityOperationType.DELETE));
        }
        else throw new RuntimeException("Category with ID " + id + " not found!");
    }
}