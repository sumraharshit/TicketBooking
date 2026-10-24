package com.booking.ticketBooking.booking.scheduler;

import com.booking.ticketBooking.booking.entity.BookingHistory;
import com.booking.ticketBooking.booking.repository.BookingRepository;
import com.booking.ticketBooking.booking.dto.BookingCompleteDto;
import com.booking.ticketBooking.booking.service.SeatLockingService;
import com.booking.ticketBooking.booking.utils.BookingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class ExpiredBookingScheduler {

    private final BookingRepository bookingRepository;

    private final SeatLockingService seatLockingService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public ExpiredBookingScheduler(BookingRepository bookingRepository, SeatLockingService seatLockingService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.bookingRepository = bookingRepository;
        this.seatLockingService = seatLockingService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 2 * 60 * 100)
    @Transactional
    public void expiredBookingScheduler(){

        LocalDateTime now = LocalDateTime.now();

       List<BookingHistory> allExpiredHistory = bookingRepository.findByStatusAndExpiresAt(BookingStatus.INITIATED, now);

       if(allExpiredHistory == null || allExpiredHistory.isEmpty()){
           log.info("There are no expired booking left in db");
           return;
       }

       allExpiredHistory.forEach(expired ->

       {
           if(processExpiredBooking(expired)){

               log.info("The DB experieced skew, redis still has lock");
           }


           expired.setStatus(BookingStatus.FAILED);
           bookingRepository.save(expired);

           log.info("The booking with id {} has been marked for failed", expired.getId());


           kafkaTemplate.send("booking.failed", expired.getId().toString(), new BookingCompleteDto(expired.getUserId(), expired.getId(), expired.getTicketId(), BookingStatus.FAILED));



       });

    }


    public boolean processExpiredBooking(BookingHistory history){

        boolean lockExists = seatLockingService.checkIfLockExists(history.getTicketId(), history.getId());

        return lockExists;
    }
}
