package com.booking.ticketBooking.booking.service;

import com.booking.ticketBooking.booking.bookingEvents.BookingEventPublisher;
import com.booking.ticketBooking.booking.dto.BookingCompleteDto;
import com.booking.ticketBooking.booking.dto.BookingPaymentRequest;
import com.booking.ticketBooking.booking.dto.BookingRequest;
import com.booking.ticketBooking.booking.entity.BookingHistory;
import com.booking.ticketBooking.booking.repository.BookingRepository;
import com.booking.ticketBooking.booking.utils.BookingStatus;
import com.booking.ticketBooking.payment.dto.PaymentResponse;
import com.booking.ticketBooking.payment.service.PaymentService;
import com.booking.ticketBooking.ticket.entity.Ticket;
import com.booking.ticketBooking.ticket.service.TicketService;
import com.booking.ticketBooking.util.Constant;
import com.booking.ticketBooking.ticket.utills.TicketStatus;
import com.razorpay.RazorpayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.UUID;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final TicketService ticketService;
    private final BookingRepository bookingRepository;
    private final StringRedisTemplate redis;
    private final KafkaTemplate<String, BookingCompleteDto> kafkaTemplate;
    private final PaymentService paymentService;
    private final BookingTransaction bookingTransaction;
    private final BookingEventPublisher bookingEventPublisher;



    @Autowired
    BookingService(TicketService ticketService, BookingRepository bookingRepository,  StringRedisTemplate redis, KafkaTemplate<String, BookingCompleteDto> kafkaTemplate, PaymentService paymentService, BookingTransaction bookingTransaction,BookingEventPublisher bookingEventPublisher ){
        this.ticketService = ticketService;
        this.bookingRepository = bookingRepository;
        this.redis = redis;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentService = paymentService;
        this.bookingTransaction = bookingTransaction;
        this.bookingEventPublisher = bookingEventPublisher;
    }


    //DISTRIBUTED LOCKING WITH REDIS

    public BookingPaymentRequest createBooking(BookingRequest bookingRequest) throws Exception{
        log.info("Started Booking for the userId: " + bookingRequest.userId());

        String lockName = "locks:" + "event:" + bookingRequest.eventId() + "ticket:" + bookingRequest.ticketId();
        String token = UUID.randomUUID().toString();

        try {
          Boolean ok = redis.opsForValue().setIfAbsent(lockName, token, Duration.ofMinutes(Constant.TIME_TO_LIVE));

          if(Boolean.TRUE.equals(ok)){
              log.info("The lock has been acquired");

              //Go for the Payment Option
              //Kafka will not be used here, as we want it to be a single process, and tell
              //us whether the payment us done or not. We don't want to treat the booking and payment
              //as different processes, they depend on each other. We cannot just dust off the
              //booking.
              LocalTime expiresAt =  LocalTime.now().plusMinutes(10);

              Ticket ticket = ticketService.findTicket(bookingRequest.ticketId());

              if(!ticket.getStatus().equals(TicketStatus.AVAILABLE)){
                  throw new IllegalArgumentException("The seat is not available");
              }
              BookingHistory history = persistBooking(bookingRequest.ticketId(), bookingRequest.userId(), BookingStatus.INITIATED, ticket.getPrice(), expiresAt, UUID.fromString(token));
            //  paymentCreator(ticket.getPrice());
//              ticket = ticketService.bookTicket(bookingRequest.ticketId(), bookingRequest.userId());
              return new BookingPaymentRequest(history.getId(), expiresAt);
          }

              log.info("The lock cannot be acquired");



        } catch(Exception e){
            releaseLock(lockName, token);
            System.out.println(e.getMessage());
            throw new Exception(e);


        }
//        finally {
//             releaseLock(lockName, token);
//        }

        throw new Exception(); //needs to change
    }

    private boolean releaseLock(String lockName, String token){
        Long released = redis.execute(
                Constant.RELEASE_LOCK_LUCA_SCRIPT, Collections.singletonList(lockName),token);

        return released == 1L;

    }



    private BookingHistory persistBooking(Long ticketId, Long userId, BookingStatus status, BigDecimal amount, LocalTime expiresAt, UUID bookingId){
        BookingHistory bookingHistory = new BookingHistory();
        bookingHistory.setId(bookingId);
        bookingHistory.setBookedAt(LocalDateTime.now());
        bookingHistory.setTicketId(ticketId);
        bookingHistory.setUserId(userId);
        bookingHistory.setStatus(status);
        bookingHistory.setAmount(amount);
        bookingHistory.setExpiresAt(expiresAt);
        bookingHistory =  bookingRepository.save(bookingHistory);

        return bookingHistory;

    }


    //For the future, when the user reserves the seat, the only we want to acquire the locks,
    //Once the locks are acquired we will have to go for the "Pay Now" Option, this will be a
    //seperate api call, but for now lets stick with the method call.
    public ResponseEntity<PaymentResponse> paymentCreator(String idempotenyKey, Long bookingId) throws RazorpayException, IllegalAccessException {
        BookingHistory history = bookingTransaction.verifyBookingId(bookingId);
        PaymentResponse response = paymentService.createPayment(idempotenyKey, bookingId, history.getTicketId(), history.getUserId(), history.getAmount());
        return new ResponseEntity<>(response, response.status());
    }

    public void successfulBooking(Long bookingId, Long ticketId) {

      BookingCompleteDto completeDto =  bookingTransaction.completeBooking(bookingId, ticketId);
        bookingEventPublisher.confirmedBookingEvent(completeDto);


    }
}
