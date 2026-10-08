package com.company.ordermanagement.controller;

import com.company.ordermanagement.model.Order;
import com.company.ordermanagement.service.CreateOrderRequest;
import com.company.ordermanagement.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for order management operations.
 * Exposes endpoints for creating, retrieving, and cancelling orders.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order.
     * Note: No idempotency key header handling — each POST creates a new order (BUG-003).
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders - Creating order for customer: {}", request.getCustomerId());
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Retrieves an order by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        log.info("GET /api/orders/{}", id);
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Retrieves all orders for a customer.
     */
    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByCustomer(@RequestParam String customerId) {
        log.info("GET /api/orders?customerId={}", customerId);
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }

    /**
     * Cancels an order.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable String id) {
        log.info("POST /api/orders/{}/cancel", id);
        Order order = orderService.cancelOrder(id);
        return ResponseEntity.ok(order);
    }
}
