package com.booking.ticketBooking.event.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @GetMapping("/viewTickets/{eventId}")
    public void viewTickets(@PathVariable("eventId") Long eventId){

    }
}
