package com.booking.ticketBooking.ticket.entity;

import com.booking.ticketBooking.util.TicketStatus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    private final Logger log = LoggerFactory.getLogger(TicketService.class);

    @Transactional
    public void seatLocking(Long eventId, Long ticketId, Long userId) throws Exception {

        if (ticketId != null) {
            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                    () -> new IllegalArgumentException("The ticket Id is wrong")
            );


            if (ticket.getStatus() == TicketStatus.AVAILABLE) {

                log.info("The seat has been booked for the user: " + userId);

                ticket.setStatus(TicketStatus.BOOKED);
                ticket.setUserId(userId);

                log.info("Booking successful for the user: " + userId);


            } else {
                throw new Exception("The ticket is already Booked");
            }
        }
    }

}
