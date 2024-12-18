package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import org.dre0065.Model.MMAFight;
import org.dre0065.Model.Fight;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Repository.MMAFightRepository;
import org.dre0065.Event.EntityAddedEvent;
import org.dre0065.Event.EntityOperationType;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.boot.context.event.*;
import java.io.*;
import java.util.*;

@Service
public class MMAFightService
{
    private static final Logger logger = LoggerFactory.getLogger(MMAFightService.class);

    @Autowired
    private MMAFightRepository mmaFightRepository;

    @Autowired
    private FightService fightService;

    @Autowired
    private MMAFighterService mmaFighterService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {loadMMAFightsFromJson();}

    @Transactional
    public void loadMMAFightsFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ClassPathResource resource = new ClassPathResource("mma_fights.json");
            List<MMAFight> mmaFightsFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<MMAFight>>() {});

            for(MMAFight mmaFightFromJson : mmaFightsFromJson)
            {
                Fight fight = fightService.getFightById(mmaFightFromJson.getFight().getFightId());
                MMAFighter fighter = mmaFighterService.getFighterByName(mmaFightFromJson.getFighter().getFirstName(), mmaFightFromJson.getFighter().getLastName());

                if(fight != null && fighter != null)
                {
                    Optional<MMAFight> existingMMAFightOpt = mmaFightRepository.findByFightAndFighter(fight, fighter);
                    if(existingMMAFightOpt.isPresent())
                    {
                        MMAFight existingMMAFight = existingMMAFightOpt.get();
                        mmaFightRepository.save(existingMMAFight);
                        eventPublisher.publishEvent(new EntityAddedEvent(this, existingMMAFight, EntityOperationType.UPDATE));
                        logger.info("Updated MMAFight with ID: {}", existingMMAFight.getMmaFightId());
                    }
                    else
                    {
                        MMAFight mmaFightToSave = MMAFight.createMMAFight(fight, fighter);
                        mmaFightRepository.save(mmaFightToSave);
                        eventPublisher.publishEvent(new EntityAddedEvent(this, mmaFightToSave, EntityOperationType.CREATE));
                        logger.info("Created MMAFight with ID: {}", mmaFightToSave.getMmaFightId());
                    }
                }
                else logger.error("Fight or Fighter not found for MMAFight with ID: {}", mmaFightFromJson.getMmaFightId());
            }
        }
        catch(IOException e) {logger.error("Error loading MMAFights from JSON: {}", e.getMessage());}
    }

    @Transactional
    public void saveAllMMAFights(List<MMAFight> mmaFights)
    {
        List<MMAFight> mmaFightsToSave = new ArrayList<>();
        for(MMAFight mmaFight : mmaFights)
        {
            Fight fight = mmaFight.getFight();
            MMAFighter fighter = mmaFight.getFighter();
            MMAFight mmaFightBuilt = MMAFight.createMMAFight(fight, fighter);
            mmaFightsToSave.add(mmaFightBuilt);
        }
        mmaFightRepository.saveAll(mmaFightsToSave);
        for(MMAFight savedMMAFight : mmaFightsToSave)
        {
            eventPublisher.publishEvent(new EntityAddedEvent(this, savedMMAFight, EntityOperationType.CREATE));
            logger.info("Created MMAFight with ID: {}", savedMMAFight.getMmaFightId());
        }
    }

    @Transactional(readOnly = true)
    public List<MMAFight> getAllMMAFights() {return mmaFightRepository.findAll();}

    @Transactional
    public void saveMMAFight(MMAFight mmaFight)
    {
        boolean isCreate = mmaFight.getMmaFightId() == 0;
        mmaFightRepository.save(mmaFight);
        EntityOperationType operationType = isCreate ? EntityOperationType.CREATE : EntityOperationType.UPDATE;
        eventPublisher.publishEvent(new EntityAddedEvent(this, mmaFight, operationType));
        logger.info((isCreate ? "Created" : "Updated") + " MMAFight with ID: {}", mmaFight.getMmaFightId());
    }

    @Transactional
    public void deleteMMAFightById(int mmaFightId)
    {
        Optional<MMAFight> mmaFightOpt = mmaFightRepository.findById(mmaFightId);
        if(mmaFightOpt.isPresent())
        {
            MMAFight mmaFight = mmaFightOpt.get();
            mmaFightRepository.delete(mmaFight);
            eventPublisher.publishEvent(new EntityAddedEvent(this, mmaFight, EntityOperationType.DELETE));
            logger.info("Deleted MMAFight with ID: {}", mmaFightId);
        }
        else
        {
            logger.error("MMAFight with ID {} not found for deletion.", mmaFightId);
            throw new RuntimeException("MMAFight with ID " + mmaFightId + " not found!");
        }
    }

    @Transactional(readOnly = true)
    public MMAFight getMMAFightById(int mmaFightId) {return mmaFightRepository.findById(mmaFightId).orElse(null);}
}