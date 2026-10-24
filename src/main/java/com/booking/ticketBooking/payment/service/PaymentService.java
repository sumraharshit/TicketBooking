package com.booking.ticketBooking.payment.service;


import com.booking.ticketBooking.payment.entity.Payment;
import com.booking.ticketBooking.payment.eventPublisher.PaymentEventPublisher;
import com.booking.ticketBooking.payment.dto.PaymentResponse;
import com.booking.ticketBooking.payment.utils.PaymentStatus;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import io.lettuce.core.RedisException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

@Service
public class PaymentService {


    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentProcessor paymentProcessor;


    private final StringRedisTemplate redisTemplate;

    private final RazorpayClient razorpayClient;

   private final PaymentEventPublisher paymentEventPublisher;


    public PaymentService(StringRedisTemplate redisTemplate,PaymentProcessor paymentProcessor, RazorpayClient razorpayClient, PaymentEventPublisher paymentEventPublisher) {
        this.redisTemplate = redisTemplate;
        this.paymentProcessor = paymentProcessor;
        this.razorpayClient = razorpayClient;
        this.paymentEventPublisher = paymentEventPublisher;

    }

    //booking service calls the payment -> createMethod ensures db persists and caching
    // ->when done pass on to the razorpay

    public PaymentResponse createPayment(String idempotencyKey, Long bookingId, Long ticketId, Long userId, BigDecimal amount) throws RazorpayException {

        if(amount == null){
            throw new NullPointerException("The amount cannot be null");
        }

        //BigDecimal to the integer

        Integer totalAmount = amount.setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2).intValueExact();



        //check the redis


        if(checkForIdempotency(idempotencyKey)!=null){
            log.info("The cache for the idempotency key is already available");
          return fetchDuplicateResponse(idempotencyKey);

        }

        try {

            paymentProcessor.createPaymentOrder(totalAmount, idempotencyKey, bookingId, ticketId, userId);

        } catch (DataIntegrityViolationException e){
            log.info("The duplicate payment is detected");
            return fetchDuplicateResponse(idempotencyKey);
        } catch (DataAccessException | TransactionException e) {
            // DB down, timeout, commit failure. Gateway call hua hi nahi = payment cancel
            log.error("Could not persist payment, key={}", idempotencyKey, e);
            redisTemplate.opsForValue().set(idempotencyKey, "FAILED");
            return new PaymentResponse("Please try again", null,
                    HttpStatus.SERVICE_UNAVAILABLE);

        }


        String receipt = idempotencyKey;

            try{



            JSONObject razorpayOrder = new JSONObject();
            razorpayOrder.put("amount", totalAmount); //amount in paise; //needs to be converted to Integer
            razorpayOrder.put("currency", "INR");
            razorpayOrder.put("receipt", receipt);

            //This order needs to be sent to the frontend


               Order order = razorpayClient.orders.create(razorpayOrder);
               paymentProcessor.persistOrderId(order.get("id"), idempotencyKey);
               redisTemplate.opsForValue().setIfAbsent(idempotencyKey, order.get("id"), Duration.ofMinutes(5));
               log.info(order.toString());
               return new PaymentResponse("The Payment has been created", order, HttpStatus.CREATED);

        } catch (RazorpayException e){
            log.error("Razorpay order creation failed", e);
            paymentProcessor.paymentFailed(idempotencyKey);
                redisTemplate.opsForValue().set(idempotencyKey, "FAILED");
            return new PaymentResponse("Payment gateway error: " + e.getMessage(), null,
                    HttpStatus.BAD_GATEWAY);
        }
        catch (RuntimeException e) {
            paymentProcessor.paymentFailed(idempotencyKey);
            releaseIdempotencyKey(idempotencyKey);
            throw new RuntimeException(e);
        }


    }


    public RedirectView verifyPayment(String razorpay_payment_id, String razorpay_order_id, String razorpay_signature) throws RazorpayException {

        Payment payment = paymentProcessor.verifyPayment(razorpay_payment_id,razorpay_order_id,razorpay_signature);

        if(payment.getPaymentStatus().equals(PaymentStatus.SUCCESS)){
            //If payment got succeed then need to inform the booking table, ticket table
            //as well as the notification and ticketgenerator
            paymentEventPublisher.sendPaymentSuccessfulEvent(payment);
           return  new RedirectView("/success.html?orderId=" + razorpay_order_id);
        }

        else if(payment.getPaymentStatus().equals(PaymentStatus.RETRYABLE)){

          Order order = razorpayClient.orders.fetch(razorpay_order_id);
            releaseIdempotencyKey(order.get("receipt"));
            return new RedirectView("/retry.html?orderId=" + razorpay_order_id);
        }

        //retry ke liye, idemporency key release krni pdegi redis ke cache se
        //aur payment repo mein status created hona chahiye, tbhi retry ho payega, wrna nhi

        else
            return new RedirectView("/failure.html");

    }

    public PaymentResponse retryPayment(String orderId) throws RazorpayException {

        Order order = razorpayClient.orders.fetch(orderId);
        String idempotencyKey = order.get("receipt");
        if(checkForIdempotency(idempotencyKey)!=null){
            log.info("The cache for the idempotency key is already available");
            return fetchDuplicateResponse(idempotencyKey);
        }

        Payment payment = paymentProcessor.getPaymentForRetry(idempotencyKey);
        if(!payment.getPaymentStatus().equals(PaymentStatus.CREATED)){
            paymentProcessor.paymentFailed(idempotencyKey);
            return new PaymentResponse("The payment has been failed. please create a new payment", null, HttpStatus.BAD_REQUEST);
        }

        redisTemplate.opsForValue().setIfAbsent(idempotencyKey, order.get("id"), Duration.ofMinutes(5));
        return new PaymentResponse("The Payment has been created", order, HttpStatus.CREATED);

    }

    private PaymentResponse fetchDuplicateResponse(String idempotencyKey){
            String value = redisTemplate.opsForValue().get(idempotencyKey);
        PaymentResponse duplicatePaymentResponse;
            if("PROCESSING".equals(value)) {
               duplicatePaymentResponse = new PaymentResponse("The Payment is under the progress", null , HttpStatus.ACCEPTED);
                return duplicatePaymentResponse;
            }
             else if("FAILED".equals(value))
               return duplicatePaymentResponse = new PaymentResponse("The Payment is FAILED", null , HttpStatus.BAD_GATEWAY);

            duplicatePaymentResponse = new PaymentResponse("The payment is already created", null ,HttpStatus.CREATED);
            return duplicatePaymentResponse;
    }

    private String checkForIdempotency(String idempotencyKey){
        String keyExists = null;

        try{

            keyExists = redisTemplate.opsForValue().get(idempotencyKey);

        } catch (RedisException e){
            throw new RedisException("Redis failed to read the data");
        }

        return keyExists;
    }

    private void releaseIdempotencyKey(String idempotencyKey){
        redisTemplate.delete(idempotencyKey);
    }

}
