package com.booking.ticketBooking.event.service;

import com.booking.ticketBooking.event.dto.PerformerDto;

public interface PerformerService {

    PerformerDto viewPerformer(Long id);

    String addPerformer(PerformerDto performerDto);

     void deletePerformer(Long id);
}
