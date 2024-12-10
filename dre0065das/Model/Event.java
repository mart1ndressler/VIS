package org.dre0065.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "event")
@JsonIgnoreProperties({"fights"})
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private int eventId;

    @Column(name = "event_name", nullable = false, length = 20)
    private String eventName;

    @Column(name = "mma_organization", nullable = false, length = 20)
    private String mmaOrganization;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_of_event", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startOfEvent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_of_event", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date endOfEvent;

    @Column(name = "location", nullable = false, length = 50)
    private String location;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Fight> fights = new HashSet<>();

    // Defaultní konstruktor pro JPA a Jackson
    protected Event() {
        // Tento konstruktor je potřeba pro JPA a Jackson
    }

    // Tovární metoda pro vytváření nových instancí Event
    public static Event createEvent(String eventName, String mmaOrganization, Date startOfEvent, Date endOfEvent, String location) {
        Event event = new Event();
        event.setEventName(eventName);
        event.setMmaOrganization(mmaOrganization);
        event.setStartOfEvent(startOfEvent);
        event.setEndOfEvent(endOfEvent);
        event.setLocation(location);
        return event;
    }
}
