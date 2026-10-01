package com.booking.ticketBooking.booking.service;

import com.booking.ticketBooking.booking.dto.BookingRequest;
import com.booking.ticketBooking.booking.entity.BookingHistory;
import com.booking.ticketBooking.booking.repository.BookingRepository;
import com.booking.ticketBooking.ticket.entity.TicketService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final TicketService ticketService;
    private final BookingRepository bookingRepository;

    @Autowired
    BookingService(TicketService ticketService, BookingRepository bookingRepository){
        this.ticketService = ticketService;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public String createBooking(BookingRequest bookingRequest) throws Exception{
        log.info("Started Booking for the userId: " + bookingRequest.userId());
        ticketService.seatLocking(bookingRequest.eventId(), bookingRequest.ticketId(), bookingRequest.userId());
        log.info("Booking successfully done for userId: " + bookingRequest.userId());
       return persistBooking(bookingRequest);

    }

    private String persistBooking(BookingRequest bookingRequest){
        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.setBookedAt(LocalDateTime.now());
        System.out.println(bookingRequest.toString());
        bookingHistory.setTicketId(bookingRequest.ticketId());
        bookingHistory.setUserId(bookingRequest.userId());
      bookingHistory =  bookingRepository.save(bookingHistory);

        return "The user has done booking, with booking id: " + bookingHistory.getId();
    }
}
