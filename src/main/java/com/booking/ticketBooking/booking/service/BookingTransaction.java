package com.booking.ticketBooking.booking.service;

import com.booking.ticketBooking.booking.dto.BookingCompleteDto;
import com.booking.ticketBooking.booking.entity.BookingHistory;
import com.booking.ticketBooking.booking.repository.BookingRepository;
import com.booking.ticketBooking.booking.utils.BookingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class BookingTransaction {

    private final BookingRepository bookingRepository;

    public BookingTransaction(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public BookingHistory verifyBookingId(Long bookingId) throws IllegalAccessException {

       BookingHistory history = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NoSuchElementException("No booking with the bookingId")
        );

        if(!history.getStatus().equals(BookingStatus.INITIATED)){
           throw new IllegalAccessException("The booking is being processed/already done");
        }

        history.setStatus(BookingStatus.INITIATED);

        return history;
    }

    @Transactional
    public BookingCompleteDto completeBooking(Long bookingId, Long ticketId){
        BookingHistory booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NoSuchElementException("Booking does not exists")
        );

        if(!booking.getStatus().equals(BookingStatus.INITIATED))
        {
            throw new IllegalArgumentException("The booking is already success/failed");
        }

        booking.setStatus(BookingStatus.SUCCESS);

        return new BookingCompleteDto(
                booking.getUserId(), booking.getId(), booking.getTicketId(), booking.getStatus()
        );

    }
}
