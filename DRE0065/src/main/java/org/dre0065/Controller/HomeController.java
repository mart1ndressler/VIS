package org.dre0065.Controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController
{
    @GetMapping("/")
    public String showHomePage() {return "index";}

    @GetMapping("/database")
    public String showDatabasePage() {return "database";}

    @GetMapping("/addFight")
    public String showAddFightPage() {return "addFight";}

    @GetMapping("/myFights")
    public String showMyFightsPage() {return "myFights";}
}