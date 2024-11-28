package org.dre0065.Model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "preparation")
public class Preparation
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int preparationId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startOfPreparation;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endOfPreparation;

    private String mmaClub;
    private String clubRegion;

    @ManyToOne
    @JoinColumn(name = "fighter_id", nullable = false)
    private MMAFighter fighter;

    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    public int getPreparationId() {return preparationId;}
    public Date getStartOfPreparation() {return startOfPreparation;}
    public Date getEndOfPreparation() {return endOfPreparation;}
    public String getMmaClub() {return mmaClub;}
    public String getClubRegion() {return clubRegion;}
    public MMAFighter getFighter() {return fighter;}
    public Coach getCoach() {return coach;}

    public void setPreparationId(int preparationId) {this.preparationId = preparationId;}
    public void setStartOfPreparation(Date startOfPreparation) {this.startOfPreparation = startOfPreparation;}
    public void setEndOfPreparation(Date endOfPreparation) {this.endOfPreparation = endOfPreparation;}
    public void setMmaClub(String mmaClub) {this.mmaClub = mmaClub;}
    public void setClubRegion(String clubRegion) {this.clubRegion = clubRegion;}
    public void setFighter(MMAFighter fighter) {this.fighter = fighter;}
    public void setCoach(Coach coach) {this.coach = coach;}
}