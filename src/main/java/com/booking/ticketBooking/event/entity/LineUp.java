package com.booking.ticketBooking.event.entity;


import com.booking.ticketBooking.event.utility.LineUpId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LineUp {
    //A common intermeidate entity b/w the Event and the Performer, we use intermediate entity, when we have more columns than a typical
    //@JoinTable can offer.
    //Here Since, the performer and the event can exist independently, we want something that can store event and the performers,
    //and can be deleted once the event is finsihed.

    @EmbeddedId
    private LineUpId lineUpId = new LineUpId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("performerId")
    private Performer performer;



}
