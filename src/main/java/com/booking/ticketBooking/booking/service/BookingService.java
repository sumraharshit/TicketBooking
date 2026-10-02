package com.booking.ticketBooking.booking.service;

import com.booking.ticketBooking.booking.dto.BookingRequest;
import com.booking.ticketBooking.booking.entity.BookingHistory;
import com.booking.ticketBooking.booking.repository.BookingRepository;
import com.booking.ticketBooking.ticket.entity.TicketService;
import com.booking.ticketBooking.util.Constant;
import com.booking.ticketBooking.util.TicketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final TicketService ticketService;
    private final BookingRepository bookingRepository;
    private final StringRedisTemplate redis;

    @Autowired
    BookingService(TicketService ticketService, BookingRepository bookingRepository, StringRedisTemplate redis){
        this.ticketService = ticketService;
        this.bookingRepository = bookingRepository;
        this.redis = redis;
    }


    //DISTRIBUTED LOCKING

    public void createBooking(BookingRequest bookingRequest) throws Exception{
        log.info("Started Booking for the userId: " + bookingRequest.userId());

        String lockName = "locks:" + "event:" + bookingRequest.eventId() + "ticket:" + bookingRequest.ticketId();
        String token = UUID.randomUUID().toString();

        try {
          Boolean ok = redis.opsForValue().setIfAbsent(lockName, token, Duration.ofMinutes(Constant.TIME_TO_LIVE));

          if(Boolean.TRUE.equals(ok)){
              log.info("The lock has been acquired");
              TicketStatus status = ticketService.bookTicket(bookingRequest.ticketId(), bookingRequest.userId());
              if(status == TicketStatus.BOOKED){
                  persistBooking(bookingRequest.ticketId(), bookingRequest.userId());
                  log.info("The booking has been completed for the userId :" + bookingRequest.userId()
                  + " with tickedId: " + bookingRequest.ticketId());
              }
          }
          else {
              log.info("The lock cannot be acquired");
          }


        } catch(Exception e){

            System.out.println(e.getMessage());

        }
        finally {
             releaseLock(lockName, token);
        }


    }

    private boolean releaseLock(String lockName, String token){
        Long released = redis.execute(
                Constant.RELEASE_LOCK_LUCA_SCRIPT, Collections.singletonList(lockName),token);

        return released == 1L;

    }

    public String persistBooking(Long ticketId, Long userId){
        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.setBookedAt(LocalDateTime.now());
        bookingHistory.setTicketId(ticketId);
        bookingHistory.setUserId(userId);
        bookingHistory =  bookingRepository.save(bookingHistory);

        return "The user has done booking, with booking id: " + bookingHistory.getId();
    }
}
