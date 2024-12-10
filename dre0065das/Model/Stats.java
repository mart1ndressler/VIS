package org.dre0065.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "stats")
public class Stats {
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

    // Defaultní konstruktor pro JPA a Jackson
    protected Stats() {
        // Tento konstruktor je potřeba pro JPA a Jackson
    }

    // Tovární metoda pro vytváření nových instancí Stats
    public static Stats createStats(int wins, int losses, int draws, int kos, int tkos, int submissions, int decisions, MMAFighter fighter) {
        Stats stats = new Stats();
        stats.setWins(wins);
        stats.setLosses(losses);
        stats.setDraws(draws);
        stats.setKos(kos);
        stats.setTkos(tkos);
        stats.setSubmissions(submissions);
        stats.setDecisions(decisions);
        stats.setFighter(fighter);
        return stats;
    }
}
