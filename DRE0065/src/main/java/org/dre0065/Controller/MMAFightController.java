package org.dre0065.Controller;

import jakarta.validation.*;
import org.dre0065.Model.MMAFight;
import org.dre0065.Service.MMAFightService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mmafights")
public class MMAFightController
{
    @Autowired
    private MMAFightService mmaFightService;

    @PostMapping("/add")
    public String addMMAFights(@Valid @RequestBody List<MMAFight> mmaFights)
    {
        mmaFightService.saveAllMMAFights(mmaFights);
        return "MMAFights added successfully!";
    }

    @GetMapping("/all")
    public List<MMAFight> getAllMMAFights() {return mmaFightService.getAllMMAFights();}

    @PostMapping("/load-json")
    public String loadMMAFightsFromJson()
    {
        mmaFightService.loadMMAFightsFromJson();
        return "MMAFights loaded from JSON successfully!";
    }
}