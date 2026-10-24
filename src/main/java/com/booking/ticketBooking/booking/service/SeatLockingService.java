package com.booking.ticketBooking.booking.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SeatLockingService {

    private final StringRedisTemplate redisTemplate;

    public SeatLockingService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean checkIfLockExists(Long ticketId, UUID bookingId){

       String book = redisTemplate.opsForValue().get(ticketId);

       return bookingId.toString().equals(book);

    }
}
