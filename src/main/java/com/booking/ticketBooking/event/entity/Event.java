package com.booking.ticketBooking.event.entity;

import com.booking.ticketBooking.ticket.entity.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "event")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToMany()
//    @JoinTable(
//            name = "event_performer",
//            joinColumns = @JoinColumn(name = "event_id"),
//            inverseJoinColumns = @JoinColumn(name = "performer_id")
//    )
//    private Set<Performer> performers = new HashSet<>();
    //Use when we dont have the itermediate entity

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LineUp> lineUps = new HashSet<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="venue_id", nullable = false)
    private Venue venue;

    @Column(length = 20)
    private String name;

//    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Ticket> tickets;
    //Many performers may perform in a single event. A performer row will be using the id of the event to specify/idenify the event, so id of the event will be used as the foreign key.
    //the JoinColumn is used to tell that the column name that will be used as the foreign key
    //In @ManyToOne the fetch type should be always lazy, bcz if not, when we load the child, we will also load the parent, to save the performance

    private LocalDateTime eventDateTime;


    @Column(length = 200)
    private String description;

//    public void addTicket(Ticket ticket){  //sync helper function
//        ticket.setEvent(this);
//        tickets.add(ticket);
//    }
    //whenever we use the cascadeType.ALL, we need to create the sync between the entities, that the entities will have the same data
    //persisted. That's why we add the sync helper function. When hybernate sees the cascade of type all, it tries to go through the
    //collection of the list to see what needs to be added, if we dont do the sync then the data would be lost


    public void addLineUp(Performer performer){
        LineUp lineUp = new LineUp();
        lineUp.setEvent(this);
        lineUp.setPerformer(performer);
        lineUps.add(lineUp);
    }


}
