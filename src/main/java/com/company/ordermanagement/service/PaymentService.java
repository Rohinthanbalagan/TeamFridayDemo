package com.company.ordermanagement.service;

import com.company.ordermanagement.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for payment processing operations.
 * Integrates with payment gateway for transaction processing.
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    /**
     * Processes a payment for an order.
     * Communicates with the payment gateway and returns the result.
     *
     * @param orderId the order identifier
     * @param amount the payment amount
     * @return the Payment result
     */
    public Payment processPayment(String orderId, double amount) {
        log.info("Processing payment for order {}: amount={}", orderId, amount);

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);

        try {
            // Simulate gateway call
            String gatewayResult = callPaymentGateway(orderId, amount);

            if ("APPROVED".equals(gatewayResult)) {
                payment.setStatus("APPROVED");
                payment.setTransactionId("TXN-" + System.currentTimeMillis());
                log.info("Payment approved for order {}", orderId);
            } else {
                payment.setStatus("DECLINED");
                payment.setGatewayResponse(gatewayResult);
                log.warn("Payment declined for order {}: {}", orderId, gatewayResult);
            }
        } catch (Exception e) {
            // Payment gateway timeout handling
            // Note: This catch block logs the error but returns null instead of
            // a proper failure response, which can leave orders in PENDING state
            log.error("Payment gateway timeout for order {}: {}", orderId, e.getMessage());
            return null;
        }

        return payment;
    }

    /**
     * Simulates calling an external payment gateway.
     * In production, this would make an HTTP call to the payment provider.
     */
    private String callPaymentGateway(String orderId, double amount) {
        // Simulate gateway response
        // In a real system, this would be an HTTP client call
        if (amount > 10000) {
            throw new RuntimeException("Gateway timeout: connection timed out after 30000ms");
        }
        return "APPROVED";
    }

    /**
     * Processes a refund for a payment.
     *
     * @param paymentId the payment identifier to refund
     * @return the refund result
     */
    public Payment processRefund(String paymentId) {
        log.info("Processing refund for payment: {}", paymentId);

        Payment refund = new Payment();
        refund.setStatus("REFUNDED");
        refund.setTransactionId("REF-" + System.currentTimeMillis());

        return refund;
    }
}
