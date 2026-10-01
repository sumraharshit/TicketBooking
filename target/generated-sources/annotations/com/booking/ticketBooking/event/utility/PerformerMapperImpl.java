package com.booking.ticketBooking.event.utility;

import com.booking.ticketBooking.event.dto.PerformerDto;
import com.booking.ticketBooking.event.entity.LineUp;
import com.booking.ticketBooking.event.entity.Performer;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-29T23:47:24+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PerformerMapperImpl implements PerformerMapper {

    @Override
    public PerformerDto toDto(Performer performer) {
        if ( performer == null ) {
            return null;
        }

        Set<LineUp> lineUps = null;
        String name = null;
        String category = null;

        Set<LineUp> set = performer.getLineUps();
        if ( set != null ) {
            lineUps = new LinkedHashSet<LineUp>( set );
        }
        name = performer.getName();
        category = performer.getCategory();

        PerformerDto performerDto = new PerformerDto( lineUps, name, category );

        return performerDto;
    }

    @Override
    public Performer toEntity(PerformerDto performerDto) {
        if ( performerDto == null ) {
            return null;
        }

        Performer performer = new Performer();

        Set<LineUp> set = performerDto.lineUps();
        if ( set != null ) {
            performer.setLineUps( new LinkedHashSet<LineUp>( set ) );
        }
        performer.setName( performerDto.name() );
        performer.setCategory( performerDto.category() );

        return performer;
    }
}
