package org.dre0065.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "coach")
public class Coach
{
    @Id
    private int coachId;
    private String firstName;
    private String lastName;
    private String specialization;

    public int getCoachId() {return coachId;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getSpecialization() {return specialization;}

    public void setCoachId(int coachId) {this.coachId = coachId;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setSpecialization(String specialization) {this.specialization = specialization;}
}