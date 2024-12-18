package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Repository.WeightCategoryRepository;
import org.dre0065.Event.EntityAddedEvent;
import org.dre0065.Event.EntityOperationType;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import java.io.*;
import java.util.*;

@Service
public class WeightCategoryService
{
    @Autowired
    private WeightCategoryRepository weightCategoryRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    @Order(1)
    @Transactional
    public void init() {loadUniqueWeightCategoriesFromJson();}

    @Transactional
    public void loadUniqueWeightCategoriesFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        try
        {
            ClassPathResource resource = new ClassPathResource("weight_categories.json");
            List<WeightCategory> categoriesFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<WeightCategory>>() {});

            for(WeightCategory categoryFromJson : categoriesFromJson)
            {
                boolean exists = weightCategoryRepository.existsByName(categoryFromJson.getName());
                if(!exists)
                {
                    WeightCategory categoryToSave = WeightCategory.createWeightCategory(
                            categoryFromJson.getName(),
                            categoryFromJson.getMinWeight(),
                            categoryFromJson.getMaxWeight()
                    );
                    weightCategoryRepository.save(categoryToSave);
                    eventPublisher.publishEvent(new EntityAddedEvent(this, categoryToSave, EntityOperationType.CREATE));
                }
            }
        }
        catch(IOException e) {System.err.println("Error loading weight categories from JSON: " + e.getMessage());}
    }

    @Transactional
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

    @Transactional(readOnly = true)
    public List<WeightCategory> getAllWeightCategories() {return weightCategoryRepository.findAll();}

    @Transactional(readOnly = true)
    public WeightCategory getWeightCategoryById(int id) {return weightCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Weight category with ID " + id + " not found."));}

    @Transactional(readOnly = true)
    public WeightCategory getWeightCategoryByName(String name) {return weightCategoryRepository.findByName(name).orElse(null);}

    @Transactional
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
        else return "Category with ID " + id + " not found!";
    }

    @Transactional
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