package org.dre0065.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Repository.MMAFighterRepository;
import org.dre0065.Service.WeightCategoryService;  // Import WeightCategoryService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;

@Service
public class MMAFighterService
{
    @Autowired
    private MMAFighterRepository mmaFighterRepository;

    @Autowired
    private WeightCategoryService weightCategoryService;

    @PostConstruct
    public void init() {loadUniqueFightersFromJson();}

    public void loadUniqueFightersFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ClassPathResource resource = new ClassPathResource("mma_fighters.json");
            List<MMAFighter> fightersFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<MMAFighter>>() {});

            for(MMAFighter fighterFromJson : fightersFromJson)
            {
                boolean exists = mmaFighterRepository.existsByFirstNameAndLastName(fighterFromJson.getFirstName(), fighterFromJson.getLastName());

                if(!exists)
                {
                    String categoryName = fighterFromJson.getWeightCategory().getName();
                    WeightCategory weightCategory = weightCategoryService.getWeightCategoryByName(categoryName);

                    if(weightCategory != null)
                    {
                        fighterFromJson.setWeightCategory(weightCategory);
                        mmaFighterRepository.save(fighterFromJson);
                        System.out.println("Added new fighter: " + fighterFromJson.getFirstName() + " " + fighterFromJson.getLastName());
                    }
                    else System.out.println("Weight category not found: " + categoryName);
                }
                else System.out.println("Fighter already exists: " + fighterFromJson.getFirstName() + " " + fighterFromJson.getLastName());
            }
            System.out.println("Fighters successfully processed from JSON!");
        }
        catch(IOException e) {System.err.println("Error loading fighters from JSON: " + e.getMessage());}
    }

    public void saveAllFighters(List<MMAFighter> fighters) { mmaFighterRepository.saveAll(fighters); }
    public List<MMAFighter> getAllFighters() { return mmaFighterRepository.findAll(); }
    public MMAFighter getFighterByName(String firstName, String lastName) {return mmaFighterRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);}

    public Set<String> getUniqueNationalities()
    {
        List<MMAFighter> fighters = mmaFighterRepository.findAll();
        Set<String> nationalities = new HashSet<>();
        for(MMAFighter fighter : fighters) if(fighter.getNationality() != null)  nationalities.add(fighter.getNationality());
        return nationalities;
    }
}