package org.dre0065.Model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "mma_fight")
public class MMAFight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mma_fight_id")
    private int mmaFightId;

    @ManyToOne
    @JoinColumn(name = "fight_id", nullable = false)
    private Fight fight;

    @ManyToOne
    @JoinColumn(name = "fighter_id", nullable = false)
    private MMAFighter fighter;

    // Defaultní konstruktor pro JPA a Jackson
    protected MMAFight() {
        // Tento konstruktor je potřeba pro JPA a Jackson
    }

    // Tovární metoda pro vytváření nových instancí MMAFight
    public static MMAFight createMMAFight(Fight fight, MMAFighter fighter) {
        MMAFight mmaFight = new MMAFight();
        mmaFight.setFight(fight);
        mmaFight.setFighter(fighter);
        return mmaFight;
    }
}
