package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.*;
import org.dre0065.Model.MMAFight;
import org.dre0065.Model.Fight;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Repository.MMAFightRepository;
import org.dre0065.Event.EntityAddedEvent;
import org.dre0065.Event.EntityOperationType;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import java.io.*;
import java.util.*;

@Service
public class MMAFightService
{
    @Autowired
    private MMAFightRepository mmaFightRepository;

    @Autowired
    private FightService fightService;

    @Autowired
    private MMAFighterService mmaFighterService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void init() {loadMMAFightsFromJson();}

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
                    MMAFight existingMMAFight = mmaFightRepository.findByFightAndFighter(fight, fighter).orElse(null);
                    if(existingMMAFight == null)
                    {
                        MMAFight mmaFightToSave = MMAFight.createMMAFight(fight, fighter);
                        mmaFightRepository.save(mmaFightToSave);
                        eventPublisher.publishEvent(new EntityAddedEvent(this, mmaFightToSave, EntityOperationType.CREATE));
                    }
                }
                else System.err.println("Fight or Fighter not found for MMAFight with ID: " + mmaFightFromJson.getMmaFightId());
            }
        }
        catch(IOException e) {System.err.println("Error loading MMAFights from JSON: " + e.getMessage());}
    }

    public List<MMAFight> getAllMMAFights() {return mmaFightRepository.findAll();}

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
        for(MMAFight savedMMAFight : mmaFightsToSave) eventPublisher.publishEvent(new EntityAddedEvent(this, savedMMAFight, EntityOperationType.CREATE));
    }

    public MMAFight getMMAFightById(int mmaFightId) {return mmaFightRepository.findById(mmaFightId).orElse(null);}

    public void saveMMAFight(MMAFight mmaFight)
    {
        boolean isCreate = mmaFight.getMmaFightId() == 0;
        mmaFightRepository.save(mmaFight);
        EntityOperationType operationType = isCreate ? EntityOperationType.CREATE : EntityOperationType.UPDATE;
        eventPublisher.publishEvent(new EntityAddedEvent(this, mmaFight, operationType));
    }

    public void deleteMMAFightById(int mmaFightId)
    {
        Optional<MMAFight> mmaFightOpt = mmaFightRepository.findById(mmaFightId);
        if(mmaFightOpt.isPresent())
        {
            MMAFight mmaFight = mmaFightOpt.get();
            mmaFightRepository.deleteById(mmaFightId);
            eventPublisher.publishEvent(new EntityAddedEvent(this, mmaFight, EntityOperationType.DELETE));
        }
        else throw new RuntimeException("MMAFight with ID " + mmaFightId + " not found!");
    }
}