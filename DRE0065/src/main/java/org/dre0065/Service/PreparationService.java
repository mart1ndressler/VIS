package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.*;
import org.dre0065.Model.Coach;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.Preparation;
import org.dre0065.Repository.PreparationRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;

@Service
public class PreparationService
{
    @Autowired
    private PreparationRepository preparationRepository;

    @Autowired
    private MMAFighterService mmaFighterService;

    @Autowired
    private CoachService coachService;

    @PostConstruct
    public void init() {loadPreparationsFromJson();}

    public void loadPreparationsFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ClassPathResource resource = new ClassPathResource("preparations.json");
            List<Preparation> preparationsFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Preparation>>() {});

            for (Preparation preparationFromJson : preparationsFromJson)
            {
                MMAFighter fighter = mmaFighterService.getFighterByName(
                        preparationFromJson.getFighter().getFirstName(),
                        preparationFromJson.getFighter().getLastName()
                );
                Coach coach = coachService.getCoachByName(
                        preparationFromJson.getCoach().getFirstName(),
                        preparationFromJson.getCoach().getLastName()
                );

                if(fighter != null && coach != null)
                {
                    Preparation existingPreparation = preparationRepository.findByStartOfPreparationAndEndOfPreparationAndFighterAndCoach(
                            preparationFromJson.getStartOfPreparation(),
                            preparationFromJson.getEndOfPreparation(),
                            fighter,
                            coach
                    );

                    if(existingPreparation != null)
                    {
                        existingPreparation.setMmaClub(preparationFromJson.getMmaClub());
                        existingPreparation.setClubRegion(preparationFromJson.getClubRegion());
                        preparationRepository.save(existingPreparation);
                        System.out.println("Updated preparation for fighter: " + fighter.getFirstName() + " " + fighter.getLastName());
                    }
                    else
                    {
                        preparationFromJson.setFighter(fighter);
                        preparationFromJson.setCoach(coach);
                        preparationRepository.save(preparationFromJson);
                        System.out.println("Added new preparation for fighter: " + fighter.getFirstName() + " " + fighter.getLastName());
                    }
                }
                else System.out.println("Fighter or Coach not found for preparation starting on: " + preparationFromJson.getStartOfPreparation());
            }
            System.out.println("Preparations successfully processed from JSON!");
        }
        catch(IOException e) {System.err.println("Error loading preparations from JSON: " + e.getMessage());}
    }

    public List<Preparation> getAllPreparations() {return preparationRepository.findAll();}
    public void saveAllPreparations(List<Preparation> preparations) {preparationRepository.saveAll(preparations);}
    public Preparation getPreparationById(int id) {return preparationRepository.findById(id).orElse(null);}
    public void deletePreparationById(int id)
    {
        preparationRepository.deleteById(id);
        System.out.println("Deleted preparation with ID: " + id);
    }
    public void savePreparation(Preparation preparation) {preparationRepository.save(preparation);}
}