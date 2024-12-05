package org.dre0065.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "weight_category")
@JsonIgnoreProperties({"fights", "mmaFighters"})
public class WeightCategory
{
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
}