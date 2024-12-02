package org.dre0065.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "stats")
public class Stats
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stats_id")
    private int statsId;

    @Column(name = "wins", nullable = false)
    private int wins;

    @Column(name = "losses", nullable = false)
    private int losses;

    @Column(name = "draws", nullable = false)
    private int draws;

    @Column(name = "kos", nullable = false)
    private int kos;

    @Column(name = "tkos", nullable = false)
    private int tkos;

    @Column(name = "submissions", nullable = false)
    private int submissions;

    @Column(name = "decisions", nullable = false)
    private int decisions;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fighter_id", nullable = false)
    @JsonProperty("mma-fighter")
    private MMAFighter fighter;
}