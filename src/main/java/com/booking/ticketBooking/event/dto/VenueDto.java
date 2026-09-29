package com.booking.ticketBooking.event.dto;

import com.booking.ticketBooking.event.entity.Event;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public record VenueDto(@Nullable List<Event> events,
                      @NotEmpty @NotNull String name,
                      @NotNull @NotEmpty String localAdress,
                       @NotNull @NotEmpty String city,
                       @NotNull @NotEmpty String state){
}
