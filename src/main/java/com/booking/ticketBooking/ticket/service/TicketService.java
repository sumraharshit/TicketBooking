package com.booking.ticketBooking.ticket.service;

import com.booking.ticketBooking.ticket.entity.Ticket;
import com.booking.ticketBooking.ticket.repository.TicketRepository;
import com.booking.ticketBooking.ticket.utills.TicketStatus;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.AccessException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final StringRedisTemplate redis;

//    private final UUID uuid;

    private final Logger log = LoggerFactory.getLogger(TicketService.class);

    //PESSIMISTIC LOCKING

//    @Transactional
//    public void seatLocking(Long eventId, Long ticketId, Long userId) throws Exception {
//
//        if (ticketId != null) {
//            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
//                    () -> new IllegalArgumentException("The ticket Id is wrong")
//            );
//
//
//            if (ticket.getStatus() == TicketStatus.AVAILABLE) {
//
//                log.info("The seat has been booked for the user: " + userId);
//
//                ticket.setStatus(TicketStatus.BOOKED);
//                ticket.setUserId(userId);
//
//                log.info("Booking successful for the user: " + userId);
//
//
//            } else {
//                throw new Exception("The ticket is already Booked");
//            }
//        }
//    }


    @Transactional
    public Ticket bookTicket(Long ticketId, Long userId) throws Exception{

        Ticket ticket = findTicket(ticketId);

        if(ticket.getStatus() != TicketStatus.AVAILABLE){
            throw new AccessException("Ticket is not Available");
        }

        ticket.setStatus(TicketStatus.BOOKED);
        ticket.setUserId(userId);
        ticket.setBookedAt(LocalDateTime.now());

        return ticket;
    }

    public Ticket findTicket(Long ticketId){
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                ()-> new NoSuchElementException("The ticket does not exists")
        );

        return ticket;
    }





}
