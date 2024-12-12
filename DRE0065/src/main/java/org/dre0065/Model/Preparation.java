package org.dre0065.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "preparation")
public class Preparation
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "preparation_id")
    private int preparationId;

    @JsonProperty("start_of_preparation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_of_preparation", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startOfPreparation;

    @JsonProperty("end_of_preparation")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_of_preparation", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endOfPreparation;

    @JsonProperty("mma_club")
    @Column(name = "mma_club", nullable = false, length = 50)
    private String mmaClub;

    @JsonProperty("club_region")
    @Column(name = "club_region", nullable = false, length = 20)
    private String clubRegion;

    @ManyToOne
    @JsonProperty("mma-fighter")
    @JoinColumn(name = "fighter_id", nullable = false)
    private MMAFighter fighter;

    @ManyToOne
    @JsonProperty("coach")
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    protected Preparation() {}

    public static Preparation createPreparation(Date startOfPreparation, Date endOfPreparation, String mmaClub, String clubRegion, MMAFighter fighter, Coach coach)
    {
        Preparation preparation = new Preparation();
        preparation.setStartOfPreparation(startOfPreparation);
        preparation.setEndOfPreparation(endOfPreparation);
        preparation.setMmaClub(mmaClub);
        preparation.setClubRegion(clubRegion);
        preparation.setFighter(fighter);
        preparation.setCoach(coach);
        return preparation;
    }
}