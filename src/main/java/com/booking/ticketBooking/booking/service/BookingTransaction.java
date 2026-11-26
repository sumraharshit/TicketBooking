package com.booking.ticketBooking.booking.service;

import com.booking.ticketBooking.booking.dto.BookingCompleteDto;
import com.booking.ticketBooking.booking.entity.BookingHistory;
import com.booking.ticketBooking.booking.repository.BookingRepository;
import com.booking.ticketBooking.booking.utils.BookingStatus;
import com.booking.ticketBooking.kafka.dto.OutboxEvent;
import com.booking.ticketBooking.kafka.repository.OutboxRepository;
import com.booking.ticketBooking.kafka.utils.OutboxStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BookingTransaction {

    private final BookingRepository bookingRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public BookingTransaction(BookingRepository bookingRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.bookingRepository = bookingRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
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

    @Transactional(rollbackFor = Exception.class)
    public void completeBooking(String bookingId, Long ticketId) throws JsonProcessingException {
        BookingHistory booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NoSuchElementException("Booking does not exists")
        );

        if(!booking.getStatus().equals(BookingStatus.INITIATED))
        {
            throw new IllegalArgumentException("The booking is already success/failed");
        }

        booking.setStatus(BookingStatus.SUCCESS);

        BookingCompleteDto bookingCompleteDto = new BookingCompleteDto(
                booking.getUserId(), booking.getId(), booking.getTicketId(), booking.getStatus());

        String payload = objectMapper.writeValueAsString(bookingCompleteDto);

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setPayload(payload);
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setOutboxId(UUID.randomUUID().toString());
        outboxEvent.setOutboxStatus(OutboxStatus.PENDING);
        outboxEvent.setEventType("BOOKING-COMPLETED");
        outboxEvent.setTopicName("booking.confirmed");
        outboxEvent.setEventId(booking.getId());

        outboxRepository.save(outboxEvent);



    }
}
