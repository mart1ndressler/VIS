package org.dre0065.Model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "weight_category")
public class WeightCategory
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int weightCategoryId;

    private String name;
    private String minWeight;
    private String maxWeight;

    @OneToMany(mappedBy = "weightCategory", cascade = CascadeType.ALL)
    private Set<MMAFighter> mmaFighters;

    @OneToMany(mappedBy = "weightCategory", cascade = CascadeType.ALL)
    private Set<Fight> fights;

    public int getWeightCategoryId() {return weightCategoryId;}
    public String getName() {return name;}
    public String getMinWeight() {return minWeight;}
    public String getMaxWeight() {return maxWeight;}
    public Set<MMAFighter> getMmaFighters() {return mmaFighters;}
    public Set<Fight> getFights() {return fights;}


    public void setWeightCategoryId(int weightCategoryId) {this.weightCategoryId = weightCategoryId;}
    public void setName(String name) {this.name = name;}
    public void setMinWeight(String minWeight) {this.minWeight = minWeight;}
    public void setMaxWeight(String maxWeight) {this.maxWeight = maxWeight;}
    public void setMmaFighters(Set<MMAFighter> mmaFighters) {this.mmaFighters = mmaFighters;}
    public void setFights(Set<Fight> fights) {this.fights = fights;}
}