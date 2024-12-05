package org.dre0065.Controller;

import lombok.*;
import org.dre0065.Model.Coach;
import org.dre0065.Model.MMAFighter;
import org.dre0065.Repository.CoachRepository;
import org.dre0065.Repository.MMAFighterRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController
{
    @Autowired
    private MMAFighterRepository mmaFighterRepository;

    @Autowired
    private CoachRepository coachRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String firstName, @RequestParam String lastName, HttpSession session)
    {
        if(firstName.equals("admin") && lastName.equals("admin"))
        {
            UserResponse admin = new UserResponse("Admin", "", "admin");
            session.setAttribute("user", admin);
            return ResponseEntity.ok("Login successful");
        }

        Optional<MMAFighter> fighterOpt = mmaFighterRepository.findByFirstNameAndLastName(firstName, lastName);
        if(fighterOpt.isPresent())
        {
            MMAFighter fighter = fighterOpt.get();
            UserResponse fighterUser = new UserResponse(fighter.getFirstName(), fighter.getLastName(), "fighter");
            session.setAttribute("user", fighterUser);
            return ResponseEntity.ok("Login successful");
        }

        Optional<Coach> coachOpt = coachRepository.findByFirstNameAndLastName(firstName, lastName);
        if(coachOpt.isPresent())
        {
            Coach coach = coachOpt.get();
            UserResponse coachUser = new UserResponse(coach.getFirstName(), coach.getLastName(), "coach");
            session.setAttribute("user", coachUser);
            return ResponseEntity.ok("Login successful");
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session)
    {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(HttpSession session)
    {
        Object userObj = session.getAttribute("user");
        if(userObj instanceof UserResponse)
        {
            UserResponse user = (UserResponse) userObj;
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("No user logged in");
    }

    @Getter
    static class UserResponse
    {
        private String firstName;
        private String lastName;
        private String role;

        public UserResponse(String firstName, String lastName, String role)
        {
            this.firstName = firstName;
            this.lastName = lastName;
            this.role = role;
        }
    }
}