package com.booking.ticketBooking.event.service;

import com.booking.ticketBooking.event.dto.EventDto;
import com.booking.ticketBooking.event.entity.Event;
import com.booking.ticketBooking.event.entity.Performer;
import com.booking.ticketBooking.event.entity.Venue;
import com.booking.ticketBooking.event.repository.EventRepository;
import com.booking.ticketBooking.event.repository.PerfomerRepository;
import com.booking.ticketBooking.event.repository.VenueRepository;
import com.booking.ticketBooking.event.utility.EventMapper;
import com.booking.ticketBooking.ticket.entity.Ticket;
import com.booking.ticketBooking.ticket.entity.TicketDto;
import com.booking.ticketBooking.util.TicketStatus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
          if(dto.ticketsDto()!=null) {
              addTickets(dto.ticketsDto(), event);
          }
        });

        eventRepository.save(event);

        System.out.println("Event has been saved " + event);

    }

    public Event viewEvent(Long id){
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("The event is not found")
        );

        return event;
    }

    @Override
    @Transactional
    public void addTickets(TicketDto ticketDto, Event event) {

//        Event event = viewEvent(ticketDto.eventId());


        Long totalSeats = ticketDto.columnNumber() * ticketDto.rowNumber();

        for(int i=1;i<=totalSeats;i++){
            Character row = (char)((i / ticketDto.columnNumber()) + 'A');
            Long seatNumber = i % ticketDto.columnNumber();
            Ticket ticket = new Ticket();

            ticket.setPrice(ticketDto.price());
            ticket.setBookedAt(LocalDateTime.now());
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticket.setRow(row);
            ticket.setSeatNumber(seatNumber);

            event.addTicket(ticket);
        }
    }

}
