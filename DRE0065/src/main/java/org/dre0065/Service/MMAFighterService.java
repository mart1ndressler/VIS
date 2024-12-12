package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.*;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Repository.MMAFighterRepository;
import org.dre0065.Event.EntityAddedEvent;
import org.dre0065.Event.EntityOperationType;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import java.io.*;
import java.util.*;

@Service
public class MMAFighterService
{
    private static final Logger logger = LoggerFactory.getLogger(MMAFighterService.class);

    @Autowired
    private MMAFighterRepository mmaFighterRepository;

    @Autowired
    private WeightCategoryService weightCategoryService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
                        MMAFighter fighterToSave = MMAFighter.createMMAFighter(fighterFromJson.getFirstName(), fighterFromJson.getLastName(), fighterFromJson.getWeight(), fighterFromJson.getHeight(), fighterFromJson.getReach(), fighterFromJson.getNationality(), fighterFromJson.getRanking(), fighterFromJson.getFights(), fighterFromJson.getPoints(), weightCategory);
                        mmaFighterRepository.save(fighterToSave);
                        eventPublisher.publishEvent(new EntityAddedEvent(this, fighterToSave, EntityOperationType.CREATE));
                        logger.info("Created MMAFighter: {} {}", fighterToSave.getFirstName(), fighterToSave.getLastName());
                    }
                    else logger.error("Weight category not found: {}", categoryName);
                }
                else logger.info("MMAFighter already exists: {} {}", fighterFromJson.getFirstName(), fighterFromJson.getLastName());
            }
        }
        catch(IOException e) {logger.error("Error loading fighters from JSON: {}", e.getMessage());}
    }

    @Transactional
    public void createFighter(MMAFighter fighter)
    {
        WeightCategory weightCategory = weightCategoryService.getWeightCategoryByName(fighter.getWeightCategory().getName());
        if(weightCategory == null)
        {
            logger.error("Weight category not found: {}", fighter.getWeightCategory().getName());
            throw new IllegalArgumentException("Weight category not found: " + fighter.getWeightCategory().getName());
        }

        fighter.setFighterId(0);
        fighter.setWeightCategory(weightCategory);
        mmaFighterRepository.save(fighter);
        eventPublisher.publishEvent(new EntityAddedEvent(this, fighter, EntityOperationType.CREATE));
        logger.info("Created MMAFighter with ID: {}", fighter.getFighterId());
    }

    @Transactional
    public String updateFighterById(int id, MMAFighter updatedFighter)
    {
        Optional<MMAFighter> existingFighterOpt = mmaFighterRepository.findById(id);
        if(existingFighterOpt.isPresent())
        {
            MMAFighter existingFighter = existingFighterOpt.get();
            WeightCategory weightCategory = weightCategoryService.getWeightCategoryByName(updatedFighter.getWeightCategory().getName());
            if(weightCategory == null)
            {
                logger.error("Weight category not found: {}", updatedFighter.getWeightCategory().getName());
                return "Weight category not found: " + updatedFighter.getWeightCategory().getName();
            }

            existingFighter.setFirstName(updatedFighter.getFirstName());
            existingFighter.setLastName(updatedFighter.getLastName());
            existingFighter.setWeight(updatedFighter.getWeight());
            existingFighter.setHeight(updatedFighter.getHeight());
            existingFighter.setReach(updatedFighter.getReach());
            existingFighter.setNationality(updatedFighter.getNationality());
            existingFighter.setRanking(updatedFighter.getRanking());
            existingFighter.setFights(updatedFighter.getFights());
            existingFighter.setPoints(updatedFighter.getPoints());
            existingFighter.setWeightCategory(weightCategory);
            mmaFighterRepository.save(existingFighter);
            eventPublisher.publishEvent(new EntityAddedEvent(this, existingFighter, EntityOperationType.UPDATE));
            logger.info("Updated MMAFighter with ID: {}", existingFighter.getFighterId());
            return "Fighter updated successfully!";
        }
        else
        {
            logger.error("Fighter with ID {} not found for update.", id);
            return "Fighter with ID " + id + " not found!";
        }
    }

    public void saveAllFighters(List<MMAFighter> fighters)
    {
        List<MMAFighter> fightersToSave = new ArrayList<>();
        for(MMAFighter fighter : fighters)
        {
            WeightCategory weightCategory = weightCategoryService.getWeightCategoryByName(fighter.getWeightCategory().getName());
            if(weightCategory == null)
            {
                logger.error("Weight category not found: {}", fighter.getWeightCategory().getName());
                continue;
            }

            fighter.setFighterId(0);
            fighter.setWeightCategory(weightCategory);
            fightersToSave.add(fighter);
        }
        mmaFighterRepository.saveAll(fightersToSave);
        for(MMAFighter savedFighter : fightersToSave)
        {
            eventPublisher.publishEvent(new EntityAddedEvent(this, savedFighter, EntityOperationType.CREATE));
            logger.info("Created MMAFighter with ID: {}", savedFighter.getFighterId());
        }
    }

    public List<MMAFighter> getAllFighters() {return mmaFighterRepository.findAll();}
    public MMAFighter getFighterByName(String firstName, String lastName) {return mmaFighterRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);}

    public Set<String> getUniqueNationalities()
    {
        List<MMAFighter> fighters = mmaFighterRepository.findAll();
        Set<String> nationalities = new HashSet<>();
        for(MMAFighter fighter : fighters) if(fighter.getNationality() != null) nationalities.add(fighter.getNationality());
        return nationalities;
    }

    public void deleteFighterById(int fighterId)
    {
        Optional<MMAFighter> fighterOpt = mmaFighterRepository.findById(fighterId);
        if(fighterOpt.isPresent())
        {
            MMAFighter fighter = fighterOpt.get();
            mmaFighterRepository.delete(fighter);
            eventPublisher.publishEvent(new EntityAddedEvent(this, fighter, EntityOperationType.DELETE));
            logger.info("Deleted MMAFighter with ID: {}", fighterId);
        }
        else
        {
            logger.error("Fighter with ID {} not found for deletion.", fighterId);
            throw new IllegalArgumentException("Fighter with ID " + fighterId + " not found!");
        }
    }

    public MMAFighter getFighterById(int id) {return mmaFighterRepository.findById(id).orElse(null);}
}