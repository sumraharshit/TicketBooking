package com.booking.ticketBooking.event.controller;
import com.booking.ticketBooking.event.dto.VenueDto;
import com.booking.ticketBooking.event.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venue")
public class VenueController {

    private final VenueService venueService;

    @Autowired
    VenueController(VenueService venueService){
        this.venueService = venueService;
    }

    @GetMapping("/viewPerformer/{id}")
    public VenueDto viewVenue(@PathVariable Long id){
        return venueService.viewVenue(id);
    }


    @PostMapping("/addVenue")
    public String addVenue(@RequestBody VenueDto venueDto){
        return venueService.addVenue(venueDto);
    }

    @DeleteMapping("/deleteVenue/{id}")
    public void deleteVenue(@PathVariable Long id){

    }
}
