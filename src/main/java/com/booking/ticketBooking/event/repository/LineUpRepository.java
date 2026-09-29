package com.booking.ticketBooking.event.repository;

import com.booking.ticketBooking.event.entity.LineUp;
import com.booking.ticketBooking.event.utility.LineUpId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LineUpRepository extends JpaRepository<LineUp, LineUpId> {

}
