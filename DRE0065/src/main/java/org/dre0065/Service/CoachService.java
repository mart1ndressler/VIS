package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.*;
import jakarta.transaction.*;
import org.dre0065.Model.Coach;
import org.dre0065.Repository.CoachRepository;
import org.dre0065.Repository.PreparationRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import java.io.*;
import java.util.*;

@Service
public class CoachService
{
    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private PreparationRepository preparationRepository;

    @PostConstruct
    public void init() {loadUniqueCoachesFromJson();}

    public void loadUniqueCoachesFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ClassPathResource resource = new ClassPathResource("coaches.json");
            List<Coach> coachesFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Coach>>() {});

            for(Coach coachFromJson : coachesFromJson)
            {
                boolean exists = coachRepository.existsByFirstNameAndLastName(coachFromJson.getFirstName(), coachFromJson.getLastName());

                if(!exists)
                {
                    coachRepository.save(coachFromJson);
                    System.out.println("Added new coach: " + coachFromJson.getFirstName() + " " + coachFromJson.getLastName());
                }
                else System.out.println("Coach already exists: " + coachFromJson.getFirstName() + " " + coachFromJson.getLastName());
            }
            System.out.println("Coaches successfully processed from JSON!");
        }
        catch(IOException e) {System.err.println("Error loading coaches from JSON: " + e.getMessage());}
    }

    public void saveAllCoaches(List<Coach> coaches) {coachRepository.saveAll(coaches);}
    public List<Coach> getAllCoaches() {return coachRepository.findAll();}
    public Coach getCoachByName(String firstName, String lastName) {return coachRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);}
    public void deleteCoachById(int id)
    {
        if(coachRepository.existsById(id)) coachRepository.deleteById(id);
        else throw new RuntimeException("Coach with ID " + id + " not found!");
    }

    public String updateCoachById(int id, Coach updatedCoach)
    {
        Optional<Coach> existingCoach = coachRepository.findById(id);
        if(existingCoach.isPresent())
        {
            Coach coach = existingCoach.get();
            coach.setFirstName(updatedCoach.getFirstName());
            coach.setLastName(updatedCoach.getLastName());
            coach.setSpecialization(updatedCoach.getSpecialization());
            coachRepository.save(coach);
            return "Coach updated successfully!";
        }
        else return "Coach with ID " + id + " not found!";
    }

    public boolean hasDependencies(int coachId)
    {
        Optional<Coach> coach = coachRepository.findById(coachId);
        return coach.isPresent() && preparationRepository.existsByCoach(coach.get());
    }

    @Transactional
    public void deleteCoachWithDependencies(int coachId)
    {
        Optional<Coach> coach = coachRepository.findById(coachId);
        if(coach.isPresent())
        {
            preparationRepository.deleteByCoach(coach.get());
            coachRepository.deleteById(coachId);
        }
        else throw new RuntimeException("Coach with ID " + coachId + " not found!");
    }

    public Coach getCoachById(int id) {return coachRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Coach not found with ID: " + id));}
}