package org.dre0065.Model;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "event")
public class Event
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventId;

    private String eventName;
    private String mmaOrganization;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startOfEvent;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endOfEvent;

    private String location;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private Set<Fight> fights;

    public int getEventId() {return eventId;}
    public String getEventName() {return eventName;}
    public String getMmaOrganization() {return mmaOrganization;}
    public Date getStartOfEvent() {return startOfEvent;}
    public Date getEndOfEvent() {return endOfEvent;}
    public String getLocation() {return location;}
    public Set<Fight> getFights() {return fights;}

    public void setEventId(int eventId) {this.eventId = eventId;}
    public void setEventName(String eventName) {this.eventName = eventName;}
    public void setMmaOrganization(String mmaOrganization) {this.mmaOrganization = mmaOrganization;}
    public void setStartOfEvent(Date startOfEvent) {this.startOfEvent = startOfEvent;}
    public void setEndOfEvent(Date endOfEvent) {this.endOfEvent = endOfEvent;}
    public void setLocation(String location) {this.location = location;}
    public void setFights(Set<Fight> fights) {this.fights = fights;}
}