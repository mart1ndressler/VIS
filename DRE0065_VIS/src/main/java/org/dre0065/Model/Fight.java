package org.dre0065.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "fight")
public class Fight
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fightId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String result;
    private String typeOfResult;

    @ManyToOne
    @JoinColumn(name = "weight_category_id", nullable = false)
    private WeightCategory weightCategory;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "fight", cascade = CascadeType.ALL)
    private Set<MMAFight> mmaFights;

    public int getFightId() {return fightId;}
    public Date getDate() {return date;}
    public String getResult() {return result;}
    public String getTypeOfResult() {return typeOfResult;}
    public WeightCategory getWeightCategory() {return weightCategory;}
    public Event getEvent() {return event;}
    public Set<MMAFight> getMmaFights() {return mmaFights;}

    public void setFightId(int fightId) {this.fightId = fightId;}
    public void setDate(Date date) {this.date = date;}
    public void setResult(String result) {this.result = result;}
    public void setTypeOfResult(String typeOfResult) {this.typeOfResult = typeOfResult;}
    public void setWeightCategory(WeightCategory weightCategory) {this.weightCategory = weightCategory;}
    public void setEvent(Event event) {this.event = event;}
    public void setMmaFights(Set<MMAFight> mmaFights) {this.mmaFights = mmaFights;}
}