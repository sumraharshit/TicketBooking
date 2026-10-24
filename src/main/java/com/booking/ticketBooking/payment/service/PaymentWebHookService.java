package com.booking.ticketBooking.payment.service;

import com.booking.ticketBooking.payment.entity.Payment;
import com.booking.ticketBooking.payment.repository.PaymentRepository;
import com.booking.ticketBooking.payment.utils.PaymentStatus;
import com.booking.ticketBooking.util.Constant;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;


@Service
public class PaymentWebHookService {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebHookService.class);
    private static final Duration DEDUP_TTL = Duration.ofDays(2);
    private static final Duration LOCK_TTL = Duration.ofMinutes(2);


    public static class EventInProgressException extends RuntimeException {
        public EventInProgressException(String eventId) {
            super("Event already in progress: " + eventId);
        }
    }

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    private final PaymentRepository paymentRepository;
    private final StringRedisTemplate redis;
    private final TransactionTemplate tx; // used instead of @Transactional to avoid self-invocation issues

    public PaymentWebHookService(PaymentRepository paymentRepository,
                                  StringRedisTemplate redis,
                                  TransactionTemplate tx) {
        this.paymentRepository = paymentRepository;
        this.redis = redis;
        this.tx = tx;
    }


    public boolean isSignatureValid(String rawBody, String signature) {
        try {
            return Utils.verifyWebhookSignature(rawBody, signature, webhookSecret);
        } catch (RazorpayException e) {
            return false;
        }
    }

    public void process(String eventId, String rawBody) {
        String doneKey = "rzp:webhook:done:" + eventId;   // set only after SUCCESS
        String lockKey = "rzp:webhook:lock:" + eventId;   // set while processing
        String token = UUID.randomUUID().toString();


        if (Boolean.TRUE.equals(redis.hasKey(doneKey))) {
            log.info("Duplicate webhook ignored, eventId={}", eventId);
            return;
        }


        Boolean locked = redis.opsForValue().setIfAbsent(lockKey, token, LOCK_TTL);
        if (!Boolean.TRUE.equals(locked)) {
            throw new EventInProgressException(eventId);
        }

        try {

            if (Boolean.TRUE.equals(redis.hasKey(doneKey))) {
                return;
            }

            JSONObject json = new JSONObject(rawBody);
            String event = json.getString("event");
            log.info("Webhook received: event={}, eventId={}", event, eventId);

            switch (event) {
                case "payment.captured" -> tx.executeWithoutResult(status -> handleCaptured(json));
                case "payment.failed" -> handleFailedAttempt(json);
                default -> log.info("Ignoring event {}", event);
            }


            redis.opsForValue().set(doneKey, "1", DEDUP_TTL);
        } finally {

            redis.execute(Constant.RELEASE_LOCK_LUCA_SCRIPT, Collections.singletonList(lockKey), token);
        }
    }

    private void handleCaptured(JSONObject json) {
        JSONObject entity = json.getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String orderId = entity.getString("order_id");
        String razorpayPaymentId = entity.getString("id");

        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment == null) {
            throw new IllegalStateException("No payment found for orderId=" + orderId);
        }


        if (payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            log.info("Order {} already SUCCESS, nothing to do", orderId);
            return;
        }


        if (!Integer.valueOf(entity.getInt("amount")).equals(payment.getAmount())) {
            log.error("Amount mismatch for orderId={}, razorpay={}, db={}",
                    orderId, entity.getInt("amount"), payment.getAmount());
            return;
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        log.info("Payment SUCCESS via webhook, orderId={}, razorpayPaymentId={}", orderId, razorpayPaymentId);

        // TODO (your domain logic, keep it idempotent):
        //   - booking status  INITIATED -> CONFIRMED
        //   - ticket          -> BOOKED for that user
        //   - release the Redis seat lock
        //   - publish a Kafka event for notification / ticket generation
    }

    private void handleFailedAttempt(JSONObject json) {
        JSONObject entity = json.getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        log.info("Payment attempt failed, orderId={}, reason={}",
                entity.optString("order_id"), entity.optString("error_description"));
    }
}