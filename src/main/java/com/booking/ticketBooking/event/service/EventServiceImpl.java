package com.booking.ticketBooking.event.service;

import com.booking.ticketBooking.event.dto.EventDto;
import com.booking.ticketBooking.event.entity.Event;
import com.booking.ticketBooking.event.entity.Performer;
import com.booking.ticketBooking.event.entity.Venue;
import com.booking.ticketBooking.event.repository.EventRepository;
import com.booking.ticketBooking.event.repository.PerfomerRepository;
import com.booking.ticketBooking.event.repository.VenueRepository;
import com.booking.ticketBooking.event.utility.EventMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService{

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final EventMapper eventMapper;
    private final PerfomerRepository perfomerRepository;

    @Override
    public void addEvent(EventDto dto) {

        Event event = eventMapper.toEntity(dto);

        System.out.println(event);

        Venue venue = venueRepository.findById(dto.venueId()).orElseThrow(
                () -> new IllegalArgumentException("Venue Does not exists")
        );

        event.setVenue(venue);

        dto.lineUpsDto().stream().forEach(lineUp -> {

          Long id =  lineUp.perfomerId();
          Performer perfomer = perfomerRepository.findById(id).orElseThrow(
                  () -> new IllegalArgumentException("Performer does not exits")
          );

          event.addLineUp(perfomer);

        });

        eventRepository.save(event);

        System.out.println("Event has been saved " + event);

    }

    public void viewEvent(Long id){
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("The event is not found")
        );
    }

}
