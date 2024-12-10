package org.dre0065.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.dre0065.Model.Coach;
import org.dre0065.Repository.CoachRepository;
import org.dre0065.Repository.PreparationRepository;
import org.dre0065.Event.EntityAddedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CoachService {
    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private PreparationRepository preparationRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostConstruct
    public void init() {
        loadUniqueCoachesFromJson();
    }

    public void loadUniqueCoachesFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ClassPathResource resource = new ClassPathResource("coaches.json");
            List<Coach> coachesFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Coach>>() {});

            for (Coach coachFromJson : coachesFromJson) {
                boolean exists = coachRepository.existsByFirstNameAndLastName(coachFromJson.getFirstName(), coachFromJson.getLastName());

                if (!exists) {
                    Coach coachToSave = Coach.createCoach(coachFromJson.getFirstName(), coachFromJson.getLastName(), coachFromJson.getSpecialization());

                    coachRepository.save(coachToSave);
                    System.out.println("Added new coach: " + coachToSave.getFirstName() + " " + coachToSave.getLastName());

                    eventPublisher.publishEvent(new EntityAddedEvent(this, coachToSave));
                } else {
                    System.out.println("Coach already exists: " + coachFromJson.getFirstName() + " " + coachFromJson.getLastName());
                }
            }
            System.out.println("Coaches successfully processed from JSON!");
        } catch (IOException e) {
            System.err.println("Error loading coaches from JSON: " + e.getMessage());
        }
    }

    public void saveAllCoaches(List<Coach> coaches) {
        List<Coach> coachesToSave = new ArrayList<>();
        for (Coach coach : coaches) {
            Coach coachBuilt = Coach.createCoach(coach.getFirstName(), coach.getLastName(), coach.getSpecialization());
            coachesToSave.add(coachBuilt);
        }
        coachRepository.saveAll(coachesToSave);
        for (Coach savedCoach : coachesToSave) {
            eventPublisher.publishEvent(new EntityAddedEvent(this, savedCoach));
        }
    }

    public List<Coach> getAllCoaches() {
        return coachRepository.findAll();
    }

    public Coach getCoachByName(String firstName, String lastName) {
        return coachRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);
    }

    public void deleteCoachById(int id) {
        if (coachRepository.existsById(id)) {
            coachRepository.deleteById(id);
        } else {
            throw new RuntimeException("Coach with ID " + id + " not found!");
        }
    }

    public String updateCoachById(int id, Coach updatedCoach) {
        Optional<Coach> existingCoachOpt = coachRepository.findById(id);
        if (existingCoachOpt.isPresent()) {
            Coach existingCoach = existingCoachOpt.get();

            existingCoach.setFirstName(updatedCoach.getFirstName());
            existingCoach.setLastName(updatedCoach.getLastName());
            existingCoach.setSpecialization(updatedCoach.getSpecialization());

            coachRepository.save(existingCoach);
            return "Coach updated successfully!";
        } else {
            return "Coach with ID " + id + " not found!";
        }
    }

    public boolean hasDependencies(int coachId) {
        Optional<Coach> coach = coachRepository.findById(coachId);
        return coach.isPresent() && preparationRepository.existsByCoach(coach.get());
    }

    @Transactional
    public void deleteCoachWithDependencies(int coachId) {
        Optional<Coach> coachOpt = coachRepository.findById(coachId);
        if (coachOpt.isPresent()) {
            Coach coach = coachOpt.get();
            preparationRepository.deleteByCoach(coach);
            coachRepository.deleteById(coachId);
        } else {
            throw new RuntimeException("Coach with ID " + coachId + " not found!");
        }
    }

    public Coach getCoachById(int id) {
        return coachRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Coach not found with ID: " + id));
    }
}
