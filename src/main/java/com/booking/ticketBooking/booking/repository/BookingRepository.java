package com.booking.ticketBooking.booking.repository;

import com.booking.ticketBooking.booking.entity.BookingHistory;
import com.booking.ticketBooking.booking.utils.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingHistory, Long> {

    List<BookingHistory> findByStatusAndExpiresAt(BookingStatus bookingStatus, LocalDateTime now);
}
