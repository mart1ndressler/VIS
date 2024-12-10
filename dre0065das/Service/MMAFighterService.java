package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import jakarta.annotation.*;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Repository.MMAFighterRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import java.io.*;
import java.util.*;

@Service
public class MMAFighterService {
    @Autowired
    private MMAFighterRepository mmaFighterRepository;

    @Autowired
    private WeightCategoryService weightCategoryService;

    @PostConstruct
    public void init() {
        loadUniqueFightersFromJson();
    }

    public void loadUniqueFightersFromJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ClassPathResource resource = new ClassPathResource("mma_fighters.json");
            List<MMAFighter> fightersFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<MMAFighter>>() {});

            for (MMAFighter fighterFromJson : fightersFromJson) {
                boolean exists = mmaFighterRepository.existsByFirstNameAndLastName(fighterFromJson.getFirstName(), fighterFromJson.getLastName());

                if (!exists) {
                    String categoryName = fighterFromJson.getWeightCategory().getName();
                    WeightCategory weightCategory = weightCategoryService.getWeightCategoryByName(categoryName);

                    if (weightCategory != null) {
                        // Použití Factory Patternu při vytváření instance MMAFighter
                        MMAFighter fighterToSave = MMAFighter.createMMAFighter(
                                fighterFromJson.getFirstName(),
                                fighterFromJson.getLastName(),
                                fighterFromJson.getWeight(),
                                fighterFromJson.getHeight(),
                                fighterFromJson.getReach(),
                                fighterFromJson.getNationality(),
                                fighterFromJson.getRanking(),
                                fighterFromJson.getFights(),
                                fighterFromJson.getPoints(),
                                weightCategory
                        );

                        mmaFighterRepository.save(fighterToSave);
                        System.out.println("Added new fighter: " + fighterToSave.getFirstName() + " " + fighterToSave.getLastName());
                    } else {
                        System.out.println("Weight category not found: " + categoryName);
                    }
                } else {
                    System.out.println("Fighter already exists: " + fighterFromJson.getFirstName() + " " + fighterFromJson.getLastName());
                }
            }
            System.out.println("Fighters successfully processed from JSON!");
        } catch (IOException e) {
            System.err.println("Error loading fighters from JSON: " + e.getMessage());
        }
    }

    public void saveAllFighters(List<MMAFighter> fighters) {
        // Použití Factory Patternu pro každého fightera před uložením
        List<MMAFighter> fightersToSave = new ArrayList<>();
        for (MMAFighter fighter : fighters) {
            WeightCategory weightCategory = weightCategoryService.getWeightCategoryByName(fighter.getWeightCategory().getName());
            if (weightCategory == null) {
                System.out.println("Weight category not found: " + fighter.getWeightCategory().getName());
                continue;
            }

            MMAFighter fighterBuilt = MMAFighter.createMMAFighter(
                    fighter.getFirstName(),
                    fighter.getLastName(),
                    fighter.getWeight(),
                    fighter.getHeight(),
                    fighter.getReach(),
                    fighter.getNationality(),
                    fighter.getRanking(),
                    fighter.getFights(),
                    fighter.getPoints(),
                    weightCategory
            );
            fightersToSave.add(fighterBuilt);
        }
        mmaFighterRepository.saveAll(fightersToSave);
    }

    public List<MMAFighter> getAllFighters() {
        return mmaFighterRepository.findAll();
    }

    public MMAFighter getFighterByName(String firstName, String lastName) {
        return mmaFighterRepository.findByFirstNameAndLastName(firstName, lastName).orElse(null);
    }

    public Set<String> getUniqueNationalities() {
        List<MMAFighter> fighters = mmaFighterRepository.findAll();
        Set<String> nationalities = new HashSet<>();
        for (MMAFighter fighter : fighters) {
            if (fighter.getNationality() != null) {
                nationalities.add(fighter.getNationality());
            }
        }
        return nationalities;
    }

    public void deleteFighterById(int fighterId) {
        MMAFighter fighter = mmaFighterRepository.findById(fighterId)
                .orElseThrow(() -> new IllegalArgumentException("Fighter with ID " + fighterId + " not found!"));
        mmaFighterRepository.delete(fighter);
        System.out.println("Deleted fighter: " + fighter.getFirstName() + " " + fighter.getLastName());
    }

    public MMAFighter getFighterById(int id) {
        return mmaFighterRepository.findById(id).orElse(null);
    }

    public void saveFighter(MMAFighter fighter) {
        WeightCategory weightCategory = weightCategoryService.getWeightCategoryByName(fighter.getWeightCategory().getName());
        if (weightCategory == null) {
            System.out.println("Weight category not found: " + fighter.getWeightCategory().getName());
            return;
        }

        // Použití Factory Patternu při vytváření instance MMAFighter
        MMAFighter fighterToSave = MMAFighter.createMMAFighter(
                fighter.getFirstName(),
                fighter.getLastName(),
                fighter.getWeight(),
                fighter.getHeight(),
                fighter.getReach(),
                fighter.getNationality(),
                fighter.getRanking(),
                fighter.getFights(),
                fighter.getPoints(),
                weightCategory
        );

        mmaFighterRepository.save(fighterToSave);
    }

    public String updateFighterById(int id, MMAFighter updatedFighter) {
        Optional<MMAFighter> existingFighterOpt = mmaFighterRepository.findById(id);
        if (existingFighterOpt.isPresent()) {
            MMAFighter existingFighter = existingFighterOpt.get();
            WeightCategory weightCategory = weightCategoryService.getWeightCategoryByName(updatedFighter.getWeightCategory().getName());
            if (weightCategory == null) {
                return "Weight category not found: " + updatedFighter.getWeightCategory().getName();
            }

            // Aktualizace atributů pomocí setterů
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
            return "Fighter updated successfully!";
        } else {
            return "Fighter with ID " + id + " not found!";
        }
    }
}
