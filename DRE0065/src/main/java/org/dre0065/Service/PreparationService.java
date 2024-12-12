package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.*;
import org.dre0065.Model.Preparation;
import org.dre0065.Model.Coach;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Repository.PreparationRepository;
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
public class PreparationService
{
    private static final Logger logger = LoggerFactory.getLogger(PreparationService.class);

    @Autowired
    private PreparationRepository preparationRepository;

    @Autowired
    private MMAFighterService mmaFighterService;

    @Autowired
    private CoachService coachService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void init() {loadPreparationsFromJson();}

    public void loadPreparationsFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ClassPathResource resource = new ClassPathResource("preparations.json");
            List<Preparation> preparationsFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Preparation>>() {});

            for(Preparation preparationFromJson : preparationsFromJson)
            {
                MMAFighter fighter = mmaFighterService.getFighterByName(preparationFromJson.getFighter().getFirstName(), preparationFromJson.getFighter().getLastName());
                Coach coach = coachService.getCoachByName(preparationFromJson.getCoach().getFirstName(), preparationFromJson.getCoach().getLastName());

                if(fighter != null && coach != null)
                {
                    Preparation existingPreparation = preparationRepository.findByStartOfPreparationAndEndOfPreparationAndFighterAndCoach(preparationFromJson.getStartOfPreparation(), preparationFromJson.getEndOfPreparation(), fighter, coach);

                    if(existingPreparation != null)
                    {
                        existingPreparation.setMmaClub(preparationFromJson.getMmaClub());
                        existingPreparation.setClubRegion(preparationFromJson.getClubRegion());
                        preparationRepository.save(existingPreparation);
                        eventPublisher.publishEvent(new EntityAddedEvent(this, existingPreparation, EntityOperationType.UPDATE));
                        logger.info("Updated Preparation with ID: {}", existingPreparation.getPreparationId());
                    }
                    else
                    {
                        Preparation preparationToSave = Preparation.createPreparation(preparationFromJson.getStartOfPreparation(), preparationFromJson.getEndOfPreparation(), preparationFromJson.getMmaClub(), preparationFromJson.getClubRegion(), fighter, coach);
                        preparationRepository.save(preparationToSave);
                        eventPublisher.publishEvent(new EntityAddedEvent(this, preparationToSave, EntityOperationType.CREATE));
                        logger.info("Created Preparation with ID: {}", preparationToSave.getPreparationId());
                    }
                }
                else logger.error("Fighter or Coach not found for preparation starting on: {}", preparationFromJson.getStartOfPreparation());
            }
        }
        catch(IOException e) {logger.error("Error loading preparations from JSON: {}", e.getMessage());}
    }

    @Transactional
    public void createPreparation(Preparation preparation)
    {
        preparation.setPreparationId(0);
        if(preparation.getFighter() == null || preparation.getFighter().getFighterId() == 0)
        {
            logger.error("Invalid Fighter information for Preparation.");
            throw new IllegalArgumentException("Invalid Fighter information for Preparation.");
        }

        if(preparation.getCoach() == null || preparation.getCoach().getCoachId() == 0)
        {
            logger.error("Invalid Coach information for Preparation.");
            throw new IllegalArgumentException("Invalid Coach information for Preparation.");
        }

        preparationRepository.save(preparation);
        eventPublisher.publishEvent(new EntityAddedEvent(this, preparation, EntityOperationType.CREATE));
        logger.info("Created Preparation with ID: {}", preparation.getPreparationId());
    }

    @Transactional
    public void updatePreparationById(int id, Preparation updatedPreparation)
    {
        Optional<Preparation> existingPreparationOpt = preparationRepository.findById(id);
        if(existingPreparationOpt.isPresent())
        {
            Preparation existingPreparation = existingPreparationOpt.get();
            existingPreparation.setStartOfPreparation(updatedPreparation.getStartOfPreparation());
            existingPreparation.setEndOfPreparation(updatedPreparation.getEndOfPreparation());
            existingPreparation.setMmaClub(updatedPreparation.getMmaClub());
            existingPreparation.setClubRegion(updatedPreparation.getClubRegion());
            existingPreparation.setFighter(updatedPreparation.getFighter());
            existingPreparation.setCoach(updatedPreparation.getCoach());
            preparationRepository.save(existingPreparation);
            eventPublisher.publishEvent(new EntityAddedEvent(this, existingPreparation, EntityOperationType.UPDATE));
            logger.info("Updated Preparation with ID: {}", existingPreparation.getPreparationId());
        }
        else
        {
            logger.error("Preparation with ID {} not found for update.", id);
            throw new IllegalArgumentException("Preparation with ID " + id + " not found!");
        }
    }

    @Transactional
    public void deletePreparationById(int id)
    {
        Optional<Preparation> prepOpt = preparationRepository.findById(id);
        if(prepOpt.isPresent())
        {
            Preparation prep = prepOpt.get();
            preparationRepository.delete(prep);
            eventPublisher.publishEvent(new EntityAddedEvent(this, prep, EntityOperationType.DELETE));
            logger.info("Deleted Preparation with ID: {}", id);
        }
        else
        {
            logger.error("Preparation with ID {} not found for deletion.", id);
            throw new IllegalArgumentException("Preparation with ID " + id + " not found!");
        }
    }

    public List<Preparation> getAllPreparations() {return preparationRepository.findAll();}

    @Transactional
    public void saveAllPreparations(List<Preparation> preparations)
    {
        List<Preparation> preparationsToSave = new ArrayList<>();
        for(Preparation preparation : preparations)
        {
            MMAFighter fighter = preparation.getFighter();
            Coach coach = preparation.getCoach();
            Preparation preparationBuilt = Preparation.createPreparation(preparation.getStartOfPreparation(), preparation.getEndOfPreparation(), preparation.getMmaClub(), preparation.getClubRegion(), fighter, coach);
            preparationBuilt.setPreparationId(0);
            preparationsToSave.add(preparationBuilt);
        }
        preparationRepository.saveAll(preparationsToSave);
        for(Preparation savedPreparation : preparationsToSave)
        {
            eventPublisher.publishEvent(new EntityAddedEvent(this, savedPreparation, EntityOperationType.CREATE));
            logger.info("Created Preparation with ID: {}", savedPreparation.getPreparationId());
        }
    }

    public Preparation getPreparationById(int id)
    {
        Optional<Preparation> optionalPreparation = preparationRepository.findById(id);
        return optionalPreparation.orElse(null);
    }
}