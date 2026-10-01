package com.booking.ticketBooking.event.controller;

import com.booking.ticketBooking.event.dto.EventDto;
import com.booking.ticketBooking.event.entity.Event;
import com.booking.ticketBooking.event.service.EventService;
import com.booking.ticketBooking.ticket.entity.TicketDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/event")

public class EventController {


    private final EventService eventService;

    @Autowired
   EventController(EventService eventService){
       this.eventService = eventService;
}

    @GetMapping("/view/{id}")
    public Event viewEvent(@PathVariable Long id){
       return eventService.viewEvent(id);
    }

    @PostMapping("/addEvent")
    public void addNewEvent(@RequestBody EventDto eventDto){
        eventService.addEvent(eventDto);
    }
//
//    @PostMapping("/addTickets")
//    public void addTickets(@RequestBody TicketDto ticketDto){
//
//        eventService.addTickets(ticketDto);
//    }

    @PatchMapping("/updateVenue")
    public void updateVenue(){

    }

    @PatchMapping("/updatePerformer")
    public void updatePerformer(){

    }
}
