package org.dre0065.Model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mma_fighter")
public class MMAFighter
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fighterId;

    private String firstName;
    private String lastName;
    private String weight;
    private String height;
    private String reach;
    private String nationality;
    private String ranking;
    private int fights;
    private int points;

    @ManyToOne
    @JoinColumn(name = "weight_category_id", nullable = false)
    private WeightCategory weightCategory;

    @OneToOne(mappedBy = "mmaFighter", cascade = CascadeType.ALL)
    private Stats stats;

    @OneToMany(mappedBy = "mmaFighter", cascade = CascadeType.ALL)
    private Set<Preparation> preparations;

    @ManyToMany
    @JoinTable(
            name = "mma_fight",
            joinColumns = @JoinColumn(name = "fighter_id"),
            inverseJoinColumns = @JoinColumn(name = "fight_id")
    )
    private Set<Fight> fightsParticipated;

    public int getFighterId() {return fighterId;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getWeight() {return weight;}
    public String getHeight() {return height;}
    public String getReach() {return reach;}
    public String getNationality() {return nationality;}
    public String getRanking() {return ranking;}
    public int getFights() {return fights;}
    public int getPoints() {return points;}
    public WeightCategory getWeightCategory() {return weightCategory;}
    public Stats getStats() {return stats;}
    public Set<Preparation> getPreparations() {return preparations;}
    public Set<Fight> getFightsParticipated() {return fightsParticipated;}

    public void setFighterId(int fighterId) {this.fighterId = fighterId;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setWeight(String weight) {this.weight = weight;}
    public void setHeight(String height) {this.height = height;}
    public void setReach(String reach) {this.reach = reach;}
    public void setNationality(String nationality) {this.nationality = nationality;}
    public void setRanking(String ranking) {this.ranking = ranking;}
    public void setFights(int fights) {this.fights = fights;}
    public void setPoints(int points) {this.points = points;}
    public void setWeightCategory(WeightCategory weightCategory) {this.weightCategory = weightCategory;}
    public void setStats(Stats stats) {this.stats = stats;}
    public void setPreparations(Set<Preparation> preparations) {this.preparations = preparations;}
    public void setFightsParticipated(Set<Fight> fightsParticipated) {this.fightsParticipated = fightsParticipated;}
}