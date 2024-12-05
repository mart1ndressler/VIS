package org.dre0065.Model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;
import com.fasterxml.jackson.annotation.*;

@Setter
@Getter
@Entity
@Table(name = "mma_fighter")
@JsonIgnoreProperties({"stats", "preparations", "fightsParticipated"})
public class MMAFighter
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fighter_id")
    private int fighterId;

    @JsonAlias("firstname")
    @JsonProperty("first_name")
    @Column(name = "first_name", nullable = false, length = 20)
    private String firstName;

    @JsonAlias("lastname")
    @JsonProperty("last_name")
    @Column(name = "last_name", nullable = false, length = 20)
    private String lastName;

    @Column(nullable = false, length = 8)
    private String weight;

    @Column(nullable = false, length = 8)
    private String height;

    @Column(nullable = false, length = 8)
    private String reach;

    @Column(nullable = false, length = 20)
    private String nationality;

    @Column(nullable = false, length = 8)
    private String ranking;

    @Column(nullable = false)
    private int fights;

    @Column(nullable = false)
    private int points;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "weight_category_id", nullable = false)
    @JsonProperty("weight_category_id")
    @JsonAlias("weight_category")
    private WeightCategory weightCategory;

    @OneToOne(mappedBy = "fighter", cascade = CascadeType.ALL)
    private Stats stats;

    @OneToMany(mappedBy = "fighter", cascade = CascadeType.ALL)
    private Set<Preparation> preparations;

    @OneToMany(mappedBy = "fighter", cascade = CascadeType.ALL)
    private Set<MMAFight> fightsParticipated;
}