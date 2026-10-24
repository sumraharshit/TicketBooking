package com.booking.ticketBooking.payment.controller;

import com.booking.ticketBooking.payment.dto.PaymentResponse;
import com.booking.ticketBooking.payment.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/verifyPayment")
    public RedirectView verifyPayment(@RequestParam("razorpay_payment_id") String razorpay_payment_id,
                                      @RequestParam("razorpay_order_id") String razorpay_order_id, @RequestParam("razorpay_signature") String razorpay_signature) throws RazorpayException {
        return paymentService.verifyPayment(razorpay_payment_id, razorpay_order_id, razorpay_signature);
    }

    @PostMapping("/retryPayment")
    public ResponseEntity<PaymentResponse> retryPayment(@RequestParam("razorpay_order_id") String razorpay_order_id) throws RazorpayException {
        PaymentResponse response = paymentService.retryPayment(razorpay_order_id);
        return new ResponseEntity<>(response, response.status());
    }
}
