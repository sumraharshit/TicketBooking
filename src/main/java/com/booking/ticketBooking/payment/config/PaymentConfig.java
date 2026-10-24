package com.booking.ticketBooking.payment.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Value("${razorpay.key.value}")
    private String razorpayKey;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    @Bean
    RazorpayClient razorpayClient() throws RazorpayException {
        return new RazorpayClient(razorpayKey, razorpaySecret);
    }
}
