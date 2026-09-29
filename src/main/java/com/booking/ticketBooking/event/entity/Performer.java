package com.booking.ticketBooking.event.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="performer")
@Getter
@Setter
public class Performer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "performer")
    private Set<LineUp> lineUps = new HashSet<>();

//    @ManyToMany(mappedBy = "performers")
//    private Set<Event> events = new HashSet<>();
    //mappedBy is used to tell the Parent enity class like Event here, that in the child class we are using 'variable' to identify it.
    //also the mappedBy is used, as the Event does not have the foreign key, we have to tell that it is used to map, else if we not then the JOIN table will be created.
    //We are avoiding to use the only @OneToMany, or we can say building the relation unidirectional, from Parent to child, bcz,
    //if we only use the @OneToMany, when the element of the child will be inserted then, it wont be having any ForeignKey column.
    //and hibernate will try to create one, by using the UPDATE statement to query it, which results in the loss of performance.
    //we will use the cascade type all, and use the orphanRemoval as the child is completely dependent on the parent.

    private String name;

    private String category;
}
