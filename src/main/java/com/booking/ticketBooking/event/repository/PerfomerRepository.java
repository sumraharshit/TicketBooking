package com.booking.ticketBooking.event.repository;

import com.booking.ticketBooking.event.entity.Performer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfomerRepository extends JpaRepository<Performer, Long> {
}
