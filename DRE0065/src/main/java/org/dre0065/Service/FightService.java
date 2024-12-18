package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import org.dre0065.Model.Fight;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Model.Event;
import org.dre0065.Repository.FightRepository;
import org.dre0065.Event.EntityAddedEvent;
import org.dre0065.Event.EntityOperationType;
import org.dre0065.Repository.MMAFightRepository;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.boot.context.event.*;
import java.io.*;
import java.util.*;

@Service
public class FightService
{
    private static final Logger logger = LoggerFactory.getLogger(FightService.class);

    @Autowired
    private FightRepository fightRepository;

    @Autowired
    private WeightCategoryService weightCategoryService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MMAFightRepository mmaFightRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {loadFightsFromJson();}

    @Transactional
    public void loadFightsFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ClassPathResource resource = new ClassPathResource("fights.json");
            List<Fight> fightsFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Fight>>() {});

            for(Fight fightFromJson : fightsFromJson)
            {
                WeightCategory weightCategory = weightCategoryService.getWeightCategoryByName(fightFromJson.getWeightCategory().getName());
                if(weightCategory == null)
                {
                    logger.error("WeightCategory not found: " + fightFromJson.getWeightCategory().getName());
                    continue;
                }
                fightFromJson.setWeightCategory(weightCategory);

                Event event = eventService.getEventByName(fightFromJson.getEvent().getEventName());
                if(event == null)
                {
                    logger.error("Event not found: " + fightFromJson.getEvent().getEventName());
                    continue;
                }
                fightFromJson.setEvent(event);

                Optional<Fight> existingFight = fightRepository.findByDateAndWeightCategoryAndEvent(fightFromJson.getDate(), weightCategory, event);
                if(existingFight.isPresent())
                {
                    Fight fightToUpdate = existingFight.get();
                    fightToUpdate.setResult(fightFromJson.getResult());
                    fightToUpdate.setTypeOfResult(fightFromJson.getTypeOfResult());
                    fightRepository.save(fightToUpdate);
                    eventPublisher.publishEvent(new EntityAddedEvent(this, fightToUpdate, EntityOperationType.UPDATE));
                    logger.info("Updated Fight with ID: " + fightToUpdate.getFightId());
                }
                else
                {
                    Fight fightToSave = Fight.createFight(fightFromJson.getDate(), fightFromJson.getResult(), fightFromJson.getTypeOfResult(), weightCategory, event);
                    fightRepository.save(fightToSave);
                    eventPublisher.publishEvent(new EntityAddedEvent(this, fightToSave, EntityOperationType.CREATE));
                    logger.info("Created Fight with ID: " + fightToSave.getFightId());
                }
            }
        }
        catch(IOException e) {logger.error("Error loading fights from JSON: " + e.getMessage());}
    }

    @Transactional(readOnly = true)
    public List<Fight> getAllFights() {return fightRepository.findAll();}

    @Transactional
    public void saveAllFights(List<Fight> fights)
    {
        List<Fight> fightsToSave = new ArrayList<>();
        for(Fight fight : fights)
        {
            Fight fightBuilt = Fight.createFight(fight.getDate(), fight.getResult(), fight.getTypeOfResult(), fight.getWeightCategory(), fight.getEvent());
            fightsToSave.add(fightBuilt);
        }
        fightRepository.saveAll(fightsToSave);
        for(Fight savedFight : fightsToSave)
        {
            eventPublisher.publishEvent(new EntityAddedEvent(this, savedFight, EntityOperationType.CREATE));
            logger.info("Created Fight with ID: " + savedFight.getFightId());
        }
    }

    @Transactional(readOnly = true)
    public Fight getFightById(int fightId) {return fightRepository.findById(fightId).orElse(null);}

    @Transactional
    public void saveFight(Fight fight)
    {
        boolean isCreate = fight.getFightId() == 0;
        fightRepository.save(fight);
        EntityOperationType operationType = isCreate ? EntityOperationType.CREATE : EntityOperationType.UPDATE;
        eventPublisher.publishEvent(new EntityAddedEvent(this, fight, operationType));
        logger.info((isCreate ? "Created" : "Updated") + " Fight with ID: " + fight.getFightId());
    }

    @Transactional
    public void deleteFightById(int fightId)
    {
        Optional<Fight> fightOpt = fightRepository.findById(fightId);
        if(fightOpt.isPresent())
        {
            Fight fight = fightOpt.get();
            mmaFightRepository.deleteByFightId(fightId);
            fightRepository.delete(fight);
            eventPublisher.publishEvent(new EntityAddedEvent(this, fight, EntityOperationType.DELETE));
            logger.info("Deleted Fight with ID: " + fightId);
        }
        else
        {
            logger.error("Fight with ID " + fightId + " not found for deletion.");
            throw new RuntimeException("Fight with ID " + fightId + " not found!");
        }
    }
}