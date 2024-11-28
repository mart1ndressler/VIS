package org.dre0065.Model;

import javax.persistence.*;

@Entity
@Table(name = "mma_fight")
public class MMAFight
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mmaFightId;

    @ManyToOne
    @JoinColumn(name = "fight_id", nullable = false)
    private Fight fight;

    @ManyToOne
    @JoinColumn(name = "fighter_id", nullable = false)
    private MMAFighter fighter;

    public int getMmaFightId() {return mmaFightId;}
    public Fight getFight() {return fight;}
    public MMAFighter getFighter() {return fighter;}

    public void setMmaFightId(int mmaFightId) {this.mmaFightId = mmaFightId;}
    public void setFight(Fight fight) {this.fight = fight;}
    public void setFighter(MMAFighter fighter) {this.fighter = fighter;}
}