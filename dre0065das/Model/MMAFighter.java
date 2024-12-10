package org.dre0065.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "mma_fighter")
@JsonIgnoreProperties({"stats", "preparations", "fightsParticipated"})
public class MMAFighter {
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
    private Set<Preparation> preparations = new HashSet<>();

    @OneToMany(mappedBy = "fighter", cascade = CascadeType.ALL)
    private Set<MMAFight> fightsParticipated = new HashSet<>();

    // Defaultní konstruktor pro JPA a Jackson
    protected MMAFighter() {
        // Tento konstruktor je potřeba pro JPA a Jackson
    }

    // Tovární metoda pro vytváření nových instancí MMAFighter
    public static MMAFighter createMMAFighter(String firstName, String lastName, String weight, String height, String reach,
                                              String nationality, String ranking, int fights, int points, WeightCategory weightCategory) {
        MMAFighter fighter = new MMAFighter();
        fighter.setFirstName(firstName);
        fighter.setLastName(lastName);
        fighter.setWeight(weight);
        fighter.setHeight(height);
        fighter.setReach(reach);
        fighter.setNationality(nationality);
        fighter.setRanking(ranking);
        fighter.setFights(fights);
        fighter.setPoints(points);
        fighter.setWeightCategory(weightCategory);
        return fighter;
    }
}
