package org.dre0065.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "coach")
@JsonIgnoreProperties({"preparations"})
public class Coach
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coach_id")
    private int coachId;

    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @Column(name = "specialization", nullable = false, length = 20)
    private String specialization;

    protected Coach() {}

    public static Coach createCoach(String firstName, String lastName, String specialization)
    {
        Coach coach = new Coach();
        coach.setFirstName(firstName);
        coach.setLastName(lastName);
        coach.setSpecialization(specialization);
        return coach;
    }
}