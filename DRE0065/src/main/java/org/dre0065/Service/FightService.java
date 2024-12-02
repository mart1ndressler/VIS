package org.dre0065.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.dre0065.Model.Event;
import org.dre0065.Model.Fight;
import org.dre0065.Model.WeightCategory;
import org.dre0065.Repository.FightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FightService
{
    @Autowired
    private FightRepository fightRepository;

    @Autowired
    private WeightCategoryService weightCategoryService;

    @Autowired
    private EventService eventService;

    @PostConstruct
    public void init() {loadFightsFromJson();}

    public void loadFightsFromJson()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        try
        {
            ClassPathResource resource = new ClassPathResource("fights.json");
            List<Fight> fightsFromJson = objectMapper.readValue(resource.getFile(), new TypeReference<List<Fight>>() {});

            for(Fight fightFromJson : fightsFromJson)
            {
                WeightCategory weightCategory = weightCategoryService.getWeightCategoryByName(fightFromJson.getWeightCategory().getName());
                if(weightCategory == null)
                {
                    System.out.println("WeightCategory not found: " + fightFromJson.getWeightCategory().getName());
                    continue;
                }
                fightFromJson.setWeightCategory(weightCategory);

                Event event = eventService.getEventByName(fightFromJson.getEvent().getEventName());
                if(event == null)
                {
                    System.out.println("Event not found: " + fightFromJson.getEvent().getEventName());
                    continue;
                }
                fightFromJson.setEvent(event);

                Optional<Fight> existingFight = fightRepository.findByDateAndWeightCategoryAndEvent(
                        fightFromJson.getDate(),
                        weightCategory,
                        event
                );

                if(existingFight.isPresent())
                {
                    Fight fightToUpdate = existingFight.get();
                    fightToUpdate.setResult(fightFromJson.getResult());
                    fightToUpdate.setTypeOfResult(fightFromJson.getTypeOfResult());
                    fightRepository.save(fightToUpdate);
                    System.out.println("Updated existing fight for event: " + event.getEventName() + ", category: " + weightCategory.getName());
                }
                else
                {
                    fightRepository.save(fightFromJson);
                    System.out.println("Added new fight for event: " + event.getEventName() + ", category: " + weightCategory.getName());
                }
            }
            System.out.println("Fights successfully processed from JSON!");
        }
        catch (IOException e) {System.err.println("Error loading fights from JSON: " + e.getMessage());}
    }

    public List<Fight> getAllFights() { return fightRepository.findAll(); }
    public void saveAllFights(List<Fight> fights) { fightRepository.saveAll(fights); }
    public Fight getFightById(int fightId) {return fightRepository.findById(fightId).orElse(null);}
}