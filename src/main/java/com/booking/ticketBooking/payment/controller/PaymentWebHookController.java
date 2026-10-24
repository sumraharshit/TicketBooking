package com.booking.ticketBooking.payment.controller;

import com.booking.ticketBooking.payment.service.PaymentWebHookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/webhook")
public class PaymentWebHookController {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebHookController.class);

    private final PaymentWebHookService webhookService;

    public PaymentWebHookController(PaymentWebHookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/razorpay")
    public ResponseEntity<String> receive(
            @RequestBody byte[] body,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature,
            @RequestHeader(value = "X-Razorpay-Event-Id", required = false) String eventId) {

        if (signature == null || eventId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("missing headers");
        }

        String rawBody = new String(body, StandardCharsets.UTF_8);


        if (!webhookService.isSignatureValid(rawBody, signature)) {
            log.warn("Webhook with invalid signature " + eventId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid signature");
        }


        try {
            webhookService.process(eventId, rawBody);
            return ResponseEntity.ok("ok");
        } catch (PaymentWebHookService.EventInProgressException e) {

            log.info("Webhook already in progress " + eventId);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("in progress");
        } catch (Exception e) {
            log.error("Webhook processing failed " + eventId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("retry");
        }
    }
}