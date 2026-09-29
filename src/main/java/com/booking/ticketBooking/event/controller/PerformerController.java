package com.booking.ticketBooking.event.controller;

import com.booking.ticketBooking.event.dto.PerformerDto;
import com.booking.ticketBooking.event.service.PerformerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/performer")
public class PerformerController {

    private final PerformerService performerService;

    @Autowired
    PerformerController(PerformerService performerService){
        this.performerService = performerService;
    }

    @GetMapping("/viewPerformer/{id}")
    public PerformerDto viewPerformer(@PathVariable Long id){
       return performerService.viewPerformer(id);
    }


    @PostMapping("/addPerformer")
    public String addPerformer(@RequestBody PerformerDto performer){
       return performerService.addPerformer(performer);
    }

    @DeleteMapping("/deletePerformer/{id}")
    public void deletePerformer(@PathVariable Long id){

    }


}
