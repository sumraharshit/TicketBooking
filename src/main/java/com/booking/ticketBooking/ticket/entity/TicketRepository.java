package com.booking.ticketBooking.ticket.entity;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>{


    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(name="jakarta.persistence.lock.timeout",value = "3000")
    })
//    @Query(nativeQuery = true, value = "SELECT * FROM ticket WHERE id:=id")
    Optional<Ticket> findById(Long id);
}
