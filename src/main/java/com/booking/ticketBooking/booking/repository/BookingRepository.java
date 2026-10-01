package com.booking.ticketBooking.booking.repository;

import com.booking.ticketBooking.booking.entity.BookingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<BookingHistory, Long> {

}
