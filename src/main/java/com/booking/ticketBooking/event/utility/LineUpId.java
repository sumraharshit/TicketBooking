package com.booking.ticketBooking.event.utility;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

//Composite Key using both the Tables of the Event and the Performer
@Embeddable
public class LineUpId implements Serializable {

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "performer_id")
    private Long performerId;

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        else if(!(o instanceof LineUpId)) return false;
        LineUpId lineUp = (LineUpId) o;
        return Objects.equals(lineUp.eventId, eventId) && Objects.equals(lineUp.performerId,performerId);
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(eventId + performerId);
    }
    }

