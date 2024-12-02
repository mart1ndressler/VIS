package org.dre0065.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.dre0065.Model.Coach;
import org.dre0065.Repository.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;

@Service
public class CoachService
{
    @Autowired
    private CoachRepository coachRepository;

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
}