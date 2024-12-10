package org.dre0065.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "weight_category")
@JsonIgnoreProperties({"fights", "mmaFighters"})
public class WeightCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonAlias("weight_category_id")
    @Column(name = "weight_category_id")
    private Integer weightCategoryId;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "min_weight", nullable = false, length = 8)
    private String minWeight;

    @Column(name = "max_weight", nullable = false, length = 8)
    private String maxWeight;

    @OneToMany(mappedBy = "weightCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MMAFighter> mmaFighters = new HashSet<>();

    @OneToMany(mappedBy = "weightCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Fight> fights = new HashSet<>();

    // Defaultní konstruktor pro JPA a Jackson
    protected WeightCategory() {
        // Tento konstruktor je potřeba pro JPA a Jackson
    }

    // Tovární metoda pro vytváření nových instancí WeightCategory
    public static WeightCategory createWeightCategory(String name, String minWeight, String maxWeight) {
        WeightCategory category = new WeightCategory();
        category.setName(name);
        category.setMinWeight(minWeight);
        category.setMaxWeight(maxWeight);
        return category;
    }
}
