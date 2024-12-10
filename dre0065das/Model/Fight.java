package org.dre0065.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "fight")
@JsonIgnoreProperties({"mmaFights"})
public class Fight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fight_id")
    private int fightId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "result", nullable = false, length = 20)
    private String result;

    @JsonProperty("type_of_result")
    @Column(name = "type_of_result", nullable = false, length = 50)
    private String typeOfResult;

    @ManyToOne
    @JsonProperty("weight_category")
    @JoinColumn(name = "weight_category_id", nullable = false)
    private WeightCategory weightCategory;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "fight", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MMAFight> mmaFights = new HashSet<>();

    // Defaultní konstruktor pro JPA a Jackson
    protected Fight() {
        // Tento konstruktor je potřeba pro JPA a Jackson
    }

    // Tovární metoda pro vytváření nových instancí Fight
    public static Fight createFight(Date date, String result, String typeOfResult, WeightCategory weightCategory, Event event) {
        Fight fight = new Fight();
        fight.setDate(date);
        fight.setResult(result);
        fight.setTypeOfResult(typeOfResult);
        fight.setWeightCategory(weightCategory);
        fight.setEvent(event);
        return fight;
    }
}
