package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import org.dre0065.Model.Coach;
import org.dre0065.Repository.CoachRepository;
import org.dre0065.Repository.PreparationRepository;
import org.dre0065.Event.EntityAddedEvent;
import org.dre0065.Event.EntityOperationType;
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
public class CoachService
{
    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private PreparationRepository preparationRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {loadUniqueCoachesFromJson();}

    @Transactional
    public void loadUniqueCoachesFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        try
        {
            ClassPathResource resource = new ClassPathResource("coaches.json");
            List<Coach> coachesFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Coach>>() {});

            for(Coach coachFromJson : coachesFromJson)
            {
                boolean exists = coachRepository.existsByFirstNameAndLastName(coachFromJson.getFirstName(), coachFromJson.getLastName());
                if(!exists)
                {
                    Coach coachToSave = Coach.createCoach(coachFromJson.getFirstName(), coachFromJson.getLastName(), coachFromJson.getSpecialization());
                    coachRepository.save(coachToSave);
                    eventPublisher.publishEvent(new EntityAddedEvent(this, coachToSave, EntityOperationType.CREATE));
                }
            }
        }
        catch(IOException e) {System.err.println("Error loading coaches from JSON: " + e.getMessage());}
    }

    @Transactional
    public void saveAllCoaches(List<Coach> coaches)
    {
        List<Coach> coachesToSave = new ArrayList<>();
        for(Coach coach : coaches)
        {
            Coach coachBuilt = Coach.createCoach(coach.getFirstName(), coach.getLastName(), coach.getSpecialization());
            coachesToSave.add(coachBuilt);
        }
        coachRepository.saveAll(coachesToSave);
        for(Coach savedCoach : coachesToSave) eventPublisher.publishEvent(new EntityAddedEvent(this, savedCoach, EntityOperationType.CREATE));
    }

    @Transactional(readOnly = true)
    public List<Coach> getAllCoaches() {return coachRepository.findAll();}

    @Transactional(readOnly = true)
    public Coach getCoachByName(String firstName, String lastName) {return coachRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);}

    @Transactional
    public void deleteCoachById(int id)
    {
        Optional<Coach> coachOpt = coachRepository.findById(id);
        if(coachOpt.isPresent())
        {
            Coach coach = coachOpt.get();
            coachRepository.deleteById(id);
            eventPublisher.publishEvent(new EntityAddedEvent(this, coach, EntityOperationType.DELETE));
        }
        else throw new RuntimeException("Coach with ID " + id + " not found!");
    }

    @Transactional
    public String updateCoachById(int id, Coach updatedCoach)
    {
        Optional<Coach> existingCoachOpt = coachRepository.findById(id);
        if(existingCoachOpt.isPresent())
        {
            Coach existingCoach = existingCoachOpt.get();
            existingCoach.setFirstName(updatedCoach.getFirstName());
            existingCoach.setLastName(updatedCoach.getLastName());
            existingCoach.setSpecialization(updatedCoach.getSpecialization());
            coachRepository.save(existingCoach);
            eventPublisher.publishEvent(new EntityAddedEvent(this, existingCoach, EntityOperationType.UPDATE));
            return "Coach updated successfully!";
        }
        else return "Coach with ID " + id + " not found!";
    }

    @Transactional
    public boolean hasDependencies(int coachId)
    {
        Optional<Coach> coach = coachRepository.findById(coachId);
        return coach.isPresent() && preparationRepository.existsByCoach(coach.get());
    }

    @Transactional
    public void deleteCoachWithDependencies(int coachId)
    {
        Optional<Coach> coachOpt = coachRepository.findById(coachId);
        if(coachOpt.isPresent())
        {
            Coach coach = coachOpt.get();
            preparationRepository.deleteByCoach(coach);
            coachRepository.deleteById(coachId);
            eventPublisher.publishEvent(new EntityAddedEvent(this, coach, EntityOperationType.DELETE));
        }
        else throw new RuntimeException("Coach with ID " + coachId + " not found!");
    }

    @Transactional(readOnly = true)
    public Coach getCoachById(int id) {return coachRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Coach not found with ID: " + id));}
}