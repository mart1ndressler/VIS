package org.dre0065.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.dre0065.Model.Stats;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Repository.StatsRepository;
import org.dre0065.Repository.MMAFighterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;

@Service
public class StatsService
{

    @Autowired
    private StatsRepository statsRepository;

    @Autowired
    private MMAFighterRepository mmaFighterRepository;

    @PostConstruct
    public void init() {loadStatsFromJson();}

    public void loadStatsFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ClassPathResource resource = new ClassPathResource("stats.json");
            List<Stats> statsListFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Stats>>() {});

            for (Stats statsFromJsonItem : statsListFromJson)
            {
                MMAFighter fighter = mmaFighterRepository.findByFirstNameAndLastName(statsFromJsonItem.getFighter().getFirstName(), statsFromJsonItem.getFighter().getLastName()).orElse(null);

                if(fighter != null)
                {
                    statsFromJsonItem.setFighter(fighter);
                    Stats existingStats = statsRepository.findByFighter(fighter).orElse(null);

                    if(existingStats != null)
                    {
                        existingStats.setWins(statsFromJsonItem.getWins());
                        existingStats.setLosses(statsFromJsonItem.getLosses());
                        existingStats.setDraws(statsFromJsonItem.getDraws());
                        existingStats.setKos(statsFromJsonItem.getKos());
                        existingStats.setTkos(statsFromJsonItem.getTkos());
                        existingStats.setSubmissions(statsFromJsonItem.getSubmissions());
                        existingStats.setDecisions(statsFromJsonItem.getDecisions());
                        statsRepository.save(existingStats);
                        System.out.println("Updated stats for fighter: " + fighter.getFirstName() + " " + fighter.getLastName());
                    }
                    else
                    {
                        statsRepository.save(statsFromJsonItem);
                        System.out.println("Added stats for fighter: " + fighter.getFirstName() + " " + fighter.getLastName());
                    }
                }
                else System.out.println("Fighter not found for stats: " + statsFromJsonItem.getFighter().getFirstName() + " " + statsFromJsonItem.getFighter().getLastName());
            }
            System.out.println("Stats successfully processed from JSON!");
        }
        catch(IOException e) {System.err.println("Error loading stats from JSON: " + e.getMessage());}
    }

    public void saveAllStats(List<Stats> stats) {statsRepository.saveAll(stats);}
    public List<Stats> getAllStats() {return statsRepository.findAll();}
}