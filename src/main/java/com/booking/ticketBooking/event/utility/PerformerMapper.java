package com.booking.ticketBooking.event.utility;

import com.booking.ticketBooking.event.dto.PerformerDto;
import com.booking.ticketBooking.event.entity.Performer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PerformerMapper {

    PerformerDto toDto(Performer performer);

    Performer toEntity(PerformerDto performerDto);
}
