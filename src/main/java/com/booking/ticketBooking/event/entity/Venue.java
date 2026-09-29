package com.booking.ticketBooking.event.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "venue")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "venue")
    private List<Event> events = new ArrayList<>();

    private String name;

    private String localAdress;

    private String city;

    private String state;

}
