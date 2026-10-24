package com.booking.ticketBooking.ticket.repository;

import com.booking.ticketBooking.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>{


//    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
//    @QueryHints({
//            @QueryHint(name="jakarta.persistence.lock.timeout",value = "3000")
//    })
//    @Query(nativeQuery = true, value = "SELECT * FROM ticket WHERE id= :id)
//    Optional<Ticket> findById(Long id);
}
