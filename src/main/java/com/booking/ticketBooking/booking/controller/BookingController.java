package com.booking.ticketBooking.booking.controller;

import com.booking.ticketBooking.booking.dto.BookingRequest;
import com.booking.ticketBooking.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/booking")
public class BookingController {


   private final BookingService bookingService;

    @Autowired
   BookingController(BookingService bookingService){
       this.bookingService = bookingService;
   }

    @PostMapping("/createBooking")
    public void createBooking(@RequestBody BookingRequest bookingRequest) throws Exception{
        bookingService.createBooking(bookingRequest);
    }

}
