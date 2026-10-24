package com.booking.ticketBooking.booking.controller;

import com.booking.ticketBooking.booking.dto.BookingPaymentRequest;
import com.booking.ticketBooking.booking.dto.BookingRequest;
import com.booking.ticketBooking.booking.service.BookingService;
import com.booking.ticketBooking.payment.dto.PaymentResponse;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/booking")
public class BookingController {


   private final BookingService bookingService;

    @Autowired
   BookingController(BookingService bookingService){
       this.bookingService = bookingService;
   }

    @PostMapping("/createBooking")
    public ResponseEntity<BookingPaymentRequest> createBooking(@RequestBody BookingRequest bookingRequest) throws Exception{
    return new ResponseEntity<>( bookingService.createBooking(bookingRequest), HttpStatus.OK);
    }

    @PostMapping("/bookingPayment/{bookingId}")
    public ResponseEntity<PaymentResponse> paymentCreator(@RequestHeader("IdempotencyKey")String idempotencyKey, @PathVariable("bookingId") Long bookingId) throws RazorpayException, IllegalAccessException {
        return bookingService.paymentCreator(idempotencyKey, bookingId);
    }

}
