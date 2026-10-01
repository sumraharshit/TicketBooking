package com.booking.ticketBooking.ticket.entity;

import com.booking.ticketBooking.util.Constant;
import com.booking.ticketBooking.util.TicketStatus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.UUID;

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

    //DISTRIBUTED LOCKING

    @Transactional
    public void seatLocking(Long eventId, Long ticketId, Long userId) throws Exception {

        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new NoSuchElementException("No ticket exists with this ticketId")
        );

        if (ticket.getStatus() == TicketStatus.AVAILABLE) {

            String token = UUID.randomUUID().toString() + userId;
            log.info(token);
            String lockName = "locks:" + eventId + ticketId;
            log.info(lockName);
            Boolean ok = redis.opsForValue().setIfAbsent(lockName, token, Duration.ofMinutes(Constant.TIME_TO_LIVE));
            if (Boolean.TRUE.equals(ok)) {
                log.info("The lock has been acquired for the following details:" + lockName + " " + token + " " + Constant.TIME_TO_LIVE + "for" + userId);
                ticket.setUserId(userId);
                ticket.setStatus(TicketStatus.BOOKED);
                Boolean released = releaseLock(lockName, token);

               if(released){
                   log.info("The lock has been released: " + lockName + " " + token + " " + Constant.TIME_TO_LIVE);
               }
            } else {
                throw new AccessException("The seat has been acquired by someone else. Try a new seat");
            }
        }
        else{
            throw new AccessException("The ticket is not available");
        }

    }

   private boolean releaseLock(String lockName, String token){
        Long released = redis.execute(
                Constant.RELEASE_LOCK_LUCA_SCRIPT, Collections.singletonList(lockName),token);

        return released == 1L;

    }

}
