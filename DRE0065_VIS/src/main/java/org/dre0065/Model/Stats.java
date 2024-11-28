package org.dre0065.Model;

import javax.persistence.*;

@Entity
@Table(name = "stats")
public class Stats
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int statsId;

    private int wins;
    private int losses;
    private int draws;
    private int kos;
    private int tkos;
    private int submissions;
    private int decisions;

    @OneToOne
    @JoinColumn(name = "fighter_id", nullable = false)
    private MMAFighter fighter;

    public int getStatsId() {return statsId;}
    public int getWins() {return wins;}
    public int getLosses() {return losses;}
    public int getDraws() {return draws;}
    public int getKos() {return kos;}
    public int getTkos() {return tkos;}
    public int getSubmissions() {return submissions;}
    public int getDecisions() {return decisions;}
    public MMAFighter getFighter() {return fighter;}

    public void setStatsId(int statsId) {this.statsId = statsId;}
    public void setWins(int wins) {this.wins = wins;}
    public void setLosses(int losses) {this.losses = losses;}
    public void setDraws(int draws) {this.draws = draws;}
    public void setKos(int kos) {this.kos = kos;}
    public void setTkos(int tkos) {this.tkos = tkos;}
    public void setSubmissions(int submissions) {this.submissions = submissions;}
    public void setDecisions(int decisions) {this.decisions = decisions;}
    public void setFighter(MMAFighter fighter) {this.fighter = fighter;}
}