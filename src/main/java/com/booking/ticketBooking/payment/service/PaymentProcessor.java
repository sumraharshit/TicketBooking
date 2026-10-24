package com.booking.ticketBooking.payment.service;

import com.booking.ticketBooking.payment.entity.Payment;
import com.booking.ticketBooking.payment.repository.PaymentRepository;
import com.booking.ticketBooking.payment.utils.PaymentStatus;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PaymentProcessor {


    @Value("${razorpay.key.value}")
    private String razorpayKey;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;
    private final PaymentRepository paymentRepository;

    private final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);

    private final RazorpayClient razorpayClient;



    @Autowired
    public PaymentProcessor(PaymentRepository paymentRepository, RazorpayClient razorpayClient) {
        this.paymentRepository = paymentRepository;
        this.razorpayClient = razorpayClient;
    }

    @Transactional
   public Payment createPaymentOrder(Integer amount, String idempotencyKey, Long bookingId, Long ticketId, Long userId) {
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setIdempotencyKey(idempotencyKey);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setCurrency("INR");
        payment.setBookingId(bookingId);
        payment.setUserId(userId);
        payment.setTicketId(ticketId);

        paymentRepository.saveAndFlush(payment);

        //Okay, so a new concept. What happens is when we use save() from the repo.
        //The data is saved in-memory of the hibernate context, and when the transaction/action is committed
        //then only the insert statement is gone to the Database to update.

        //We want to check that the payment is already being processed/done.
        //If we use the save(), here we will be inserting the payment when the full transaction is compelted
        //that includes the call to the payment gateway, and all.
        //When using save, it will store the payment data in-memory and pass the compiler to the
        //payment gateway processing, and oncce the process is completed, then it will try to insert the
        //sql for the payment data. But if the data already exsisted, it will not inform us about it
        //at that time. Instead when it will try to insert after the transacction is ccompleted, then it
        //will inform us about it. By that time the customer will be charged again, and we dont want that
        //Instead we will use the saveAndFlush, it will immediately, take the data from in-memoery and place
        //the insert sql in the database(though it will only execcute it after the transaction is ccompleted)
        //If some other repeated request/data try to saveAndFlush, it will see the insert already there
        //and will inform us then and there, and not proceed further for the payment.

    return payment;
        }

    @Transactional(rollbackFor = Exception.class)
    public Payment verifyPayment(String razorpay_payment_id, String razorpay_order_id, String razorpay_signature) throws RazorpayException {


        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", razorpay_order_id);
        options.put("razorpay_payment_id", razorpay_payment_id);
        options.put("razorpay_signature", razorpay_signature);


        boolean verified = Utils.verifyPaymentSignature(options, razorpaySecret);
        Payment payment =  paymentRepository.findByOrderId(razorpay_order_id);

        if(payment == null){
            throw new NullPointerException("The order Id does not exits");
        }
        if(payment.getPaymentStatus() == PaymentStatus.SUCCESS)
            return payment;

        if (verified) {

             payment.setPaymentStatus(PaymentStatus.SUCCESS);
             return payment;

            } else {
          return  resolveFromGateway(razorpay_order_id);
            }


    }

    @Transactional
    public Payment paymentFailed(String idempotencyKey){
        Payment payment = paymentRepository.findByIdempotencyKey(idempotencyKey);
        payment.setPaymentStatus(PaymentStatus.FAILED);
       payment = paymentRepository.saveAndFlush(payment);
        return payment;
    }

    @Transactional
    public void persistOrderId(String id, String idempotencyKey) {
        Payment payment = paymentRepository.findByIdempotencyKey(idempotencyKey);
        payment.setPaymentStatus(PaymentStatus.CREATED);
        payment.setOrderId(id);
        paymentRepository.saveAndFlush(payment);
    }

    public Payment resolveFromGateway(String orderId) throws RazorpayException {
        Payment payment = paymentRepository.findByOrderId(orderId);
        List<com.razorpay.Payment> attempts = razorpayClient.orders.fetchPayments(orderId);

        var captured = attempts.stream()
                .filter(a -> "captured".equals(a.get("status"))).findFirst();
        boolean inFlight = attempts.stream()
                .anyMatch(a -> "authorized".equals(a.get("status")) || "created".equals(a.get("status")));

        if (captured.isPresent()) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
        }
      else if (inFlight) {
            payment.setPaymentStatus(PaymentStatus.CREATED);

        }

      else
        payment.setPaymentStatus(PaymentStatus.RETRYABLE);

        return payment;
    }

    @Transactional
    public Payment getPaymentForRetry(String idempotencyKey){
        return paymentRepository.findByIdempotencyKey(idempotencyKey);
    }

}
