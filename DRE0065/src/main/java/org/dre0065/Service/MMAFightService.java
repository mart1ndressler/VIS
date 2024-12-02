package org.dre0065.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.dre0065.Model.MMAFight;
import org.dre0065.Model.Fight;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Repository.MMAFightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class MMAFightService
{
    @Autowired
    private MMAFightRepository mmaFightRepository;

    @Autowired
    private FightService fightService;

    @Autowired
    private MMAFighterService mmaFighterService;

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
                MMAFighter fighter = mmaFighterService.getFighterByName(
                        mmaFightFromJson.getFighter().getFirstName(),
                        mmaFightFromJson.getFighter().getLastName()
                );

                if(fight != null && fighter != null)
                {
                    MMAFight existingMMAFight = mmaFightRepository.findByFightAndFighter(fight, fighter).orElse(null);

                    if(existingMMAFight != null) System.out.println("MMAFight already exists for fighter: " + fighter.getFirstName() + " " + fighter.getLastName());
                    else
                    {
                        mmaFightFromJson.setFight(fight);
                        mmaFightFromJson.setFighter(fighter);
                        mmaFightRepository.save(mmaFightFromJson);
                        System.out.println("Added new MMAFight for fighter: " + fighter.getFirstName() + " " + fighter.getLastName());
                    }
                }
                else System.out.println("Fight or Fighter not found for MMAFight with ID: " + mmaFightFromJson.getMmaFightId());
            }
            System.out.println("MMAFights successfully processed from JSON!");
        }
        catch(IOException e) {System.err.println("Error loading MMAFights from JSON: " + e.getMessage());}
        System.out.println("MMAFights loaded from JSON successfully!");
    }

    public List<MMAFight> getAllMMAFights() {return mmaFightRepository.findAll();}
    public void saveAllMMAFights(List<MMAFight> mmaFights) {mmaFightRepository.saveAll(mmaFights);}
}