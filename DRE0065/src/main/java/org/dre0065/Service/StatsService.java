package org.dre0065.Service;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import org.dre0065.Model.Stats;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Repository.StatsRepository;
import org.dre0065.Repository.MMAFighterRepository;
import org.dre0065.Event.EntityAddedEvent;
import org.dre0065.Event.EntityOperationType;
import org.slf4j.*;
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
public class StatsService
{
    private static final Logger logger = LoggerFactory.getLogger(StatsService.class);

    @Autowired
    private StatsRepository statsRepository;

    @Autowired
    private MMAFighterRepository mmaFighterRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {loadStatsFromJson();}

    @Transactional
    public void loadStatsFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ClassPathResource resource = new ClassPathResource("stats.json");
            List<Stats> statsListFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Stats>>() {});

            for(Stats statsFromJsonItem : statsListFromJson)
            {
                if(statsFromJsonItem.getFighter() == null || statsFromJsonItem.getFighter().getFirstName() == null || statsFromJsonItem.getFighter().getLastName() == null)
                {
                    logger.error("Stats record is missing fighter information. Skipping.");
                    continue;
                }

                MMAFighter fighter = mmaFighterRepository.findByFirstNameAndLastName(statsFromJsonItem.getFighter().getFirstName(), statsFromJsonItem.getFighter().getLastName()).orElse(null);
                if(fighter == null)
                {
                    logger.error("Fighter not found for stats: {} {}", statsFromJsonItem.getFighter().getFirstName(), statsFromJsonItem.getFighter().getLastName());
                    continue;
                }

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
                    existingStats.setFighter(fighter);

                    statsRepository.save(existingStats);
                    eventPublisher.publishEvent(new EntityAddedEvent(this, existingStats, EntityOperationType.UPDATE));
                    logger.info("Updated Stats for Fighter ID: {}", existingStats.getStatsId());
                }
                else
                {
                    Stats statsToSave = Stats.createStats(statsFromJsonItem.getWins(), statsFromJsonItem.getLosses(), statsFromJsonItem.getDraws(), statsFromJsonItem.getKos(), statsFromJsonItem.getTkos(), statsFromJsonItem.getSubmissions(), statsFromJsonItem.getDecisions(), fighter);
                    statsToSave.setFighter(fighter);
                    statsRepository.save(statsToSave);
                    eventPublisher.publishEvent(new EntityAddedEvent(this, statsToSave, EntityOperationType.CREATE));
                    logger.info("Created Stats for Fighter ID: {}", statsToSave.getStatsId());
                }
            }
        }
        catch(IOException e) {logger.error("Error loading stats from JSON: {}", e.getMessage());}
    }

    @Transactional
    public void createStats(Stats stats)
    {
        MMAFighter fighter = mmaFighterRepository.findById(stats.getFighter().getFighterId()).orElse(null);
        if(fighter == null)
        {
            logger.error("Fighter not found for stats creation.");
            throw new IllegalArgumentException("Fighter not found for stats creation.");
        }

        Optional<Stats> existingStatsOpt = statsRepository.findByFighter(fighter);
        if(existingStatsOpt.isPresent())
        {
            logger.error("Fighter with ID {} already has stats.", fighter.getFighterId());
            throw new IllegalStateException("Fighter with ID " + fighter.getFighterId() + " already has stats.");
        }

        Stats statsToSave = Stats.createStats(stats.getWins(), stats.getLosses(), stats.getDraws(), stats.getKos(), stats.getTkos(), stats.getSubmissions(), stats.getDecisions(), fighter);
        statsRepository.save(statsToSave);
        eventPublisher.publishEvent(new EntityAddedEvent(this, statsToSave, EntityOperationType.CREATE));
        logger.info("Created Stats for Fighter ID: {}", fighter.getFighterId());
    }

    @Transactional
    public void updateStatById(int id, Stats updatedStat)
    {
        Optional<Stats> existingStatOpt = statsRepository.findById(id);
        if(existingStatOpt.isPresent())
        {
            Stats existingStat = existingStatOpt.get();
            existingStat.setWins(updatedStat.getWins());
            existingStat.setLosses(updatedStat.getLosses());
            existingStat.setDraws(updatedStat.getDraws());
            existingStat.setKos(updatedStat.getKos());
            existingStat.setTkos(updatedStat.getTkos());
            existingStat.setSubmissions(updatedStat.getSubmissions());
            existingStat.setDecisions(updatedStat.getDecisions());

            if(updatedStat.getFighter() != null)
            {
                MMAFighter fighter = mmaFighterRepository.findById(updatedStat.getFighter().getFighterId()).orElse(null);
                if(fighter != null)
                {
                    Optional<Stats> fighterStatsOpt = statsRepository.findByFighter(fighter);
                    if(fighterStatsOpt.isPresent() && fighterStatsOpt.get().getStatsId() != id)
                    {
                        logger.error("Fighter with ID {} already has stats.", fighter.getFighterId());
                        throw new IllegalStateException("Fighter with ID " + fighter.getFighterId() + " already has stats.");
                    }
                    existingStat.setFighter(fighter);
                }
                else
                {
                    logger.error("Fighter with ID {} not found for stats update.", updatedStat.getFighter().getFighterId());
                    throw new IllegalArgumentException("Fighter with ID " + updatedStat.getFighter().getFighterId() + " not found.");
                }
            }

            statsRepository.save(existingStat);
            eventPublisher.publishEvent(new EntityAddedEvent(this, existingStat, EntityOperationType.UPDATE));
            logger.info("Updated Stats with ID: {}", existingStat.getStatsId());
        }
        else
        {
            logger.error("Stats with ID {} not found for update.", id);
            throw new IllegalArgumentException("Stats with ID " + id + " not found.");
        }
    }

    @Transactional
    public void deleteStatById(int id)
    {
        Optional<Stats> optionalStatOpt = statsRepository.findById(id);
        if(optionalStatOpt.isPresent())
        {
            Stats stat = optionalStatOpt.get();
            MMAFighter fighter = stat.getFighter();
            if(fighter != null)
            {
                fighter.setStats(null);
                mmaFighterRepository.save(fighter);
            }

            statsRepository.delete(stat);
            eventPublisher.publishEvent(new EntityAddedEvent(this, stat, EntityOperationType.DELETE));
            logger.info("Deleted Stats with ID: {}", id);
        }
        else
        {
            logger.error("Stats with ID {} not found for deletion.", id);
            throw new IllegalArgumentException("Stats with ID " + id + " not found.");
        }
    }

    @Transactional(readOnly = true)
    public List<Stats> getAllStats() {return statsRepository.findAll();}

    @Transactional
    public void saveAllStats(List<Stats> statsList)
    {
        List<Stats> statsToSave = new ArrayList<>();
        for(Stats stat : statsList)
        {
            MMAFighter fighter = stat.getFighter();
            Optional<Stats> existingStatOpt = statsRepository.findByFighter(fighter);
            if(existingStatOpt.isPresent())
            {
                logger.error("Fighter with ID {} already has stats.", fighter.getFighterId());
                continue;
            }
            Stats statBuilt = Stats.createStats(stat.getWins(), stat.getLosses(), stat.getDraws(), stat.getKos(), stat.getTkos(), stat.getSubmissions(), stat.getDecisions(), fighter);
            statsToSave.add(statBuilt);
        }
        statsRepository.saveAll(statsToSave);
        for(Stats savedStat : statsToSave)
        {
            eventPublisher.publishEvent(new EntityAddedEvent(this, savedStat, EntityOperationType.CREATE));
            logger.info("Created Stats with ID: {}", savedStat.getStatsId());
        }
    }

    @Transactional
    public void saveStat(Stats stat)
    {
        if(stat.getStatsId() == 0) createStats(stat);
        else updateStatById(stat.getStatsId(), stat);
    }

    @Transactional
    public Stats getStatsById(int id)
    {
        Optional<Stats> optionalStat = statsRepository.findById(id);
        return optionalStat.orElse(null);
    }
}