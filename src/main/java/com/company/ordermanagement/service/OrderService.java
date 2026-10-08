package com.company.ordermanagement.service;

import com.company.ordermanagement.exception.OrderValidationException;
import com.company.ordermanagement.model.*;
import com.company.ordermanagement.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Core order management service responsible for order lifecycle operations.
 * Handles order creation, retrieval, modification, and cancellation.
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;

    public OrderService(
            OrderRepository orderRepository,
            CustomerService customerService,
            InventoryService inventoryService,
            PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }

    /**
     * Creates a new order for a customer.
     *
     * Validates customer eligibility, reserves inventory, sets shipping address,
     * and persists the order.
     *
     * @param request the order creation request containing customer ID and items
     * @return the created Order entity
     * @throws OrderValidationException if the customer is not eligible
     */
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.getCustomerId());

        // Validate customer eligibility
        // Note: This calls CustomerService.isCustomerEligible() which has BUG-001
        if (!customerService.isCustomerEligible(request.getCustomerId())) {
            throw new OrderValidationException("Customer is not eligible to place orders");
        }

        // BUG-003: No idempotency check — duplicate orders will be created
        // if the client retries the request. There is no mechanism to detect
        // or prevent duplicate submissions based on an idempotency key or
        // matching customer + items within a recent time window.

        // Validate and reserve inventory
        inventoryService.reserveInventory(request.getItems());

        // Calculate total amount
        BigDecimal totalAmount = calculateTotal(request.getItems());

        // Build order
        Order order = Order.builder()
            .customerId(request.getCustomerId())
            .items(request.getItems())
            .status(OrderStatus.PENDING)
            .totalAmount(totalAmount)
            .build();

        // BUG-002: Set shipping address without null check.
        // If the customer has no address on file (registered via mobile app),
        // getCustomerAddress() returns null, and the subsequent property access
        // throws NullPointerException.
        Address shippingAddress = customerService.getCustomerAddress(request.getCustomerId());
        order.setShippingStreet(shippingAddress.getStreet());
        order.setShippingCity(shippingAddress.getCity());
        order.setShippingState(shippingAddress.getState());
        order.setShippingZip(shippingAddress.getZipCode());

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", savedOrder.getId());

        return savedOrder;
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the unique order identifier
     * @return the Order entity
     * @throws OrderValidationException if the order does not exist
     */
    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderValidationException("Order not found: " + orderId));
    }

    /**
     * Retrieves all orders for a customer.
     *
     * @param customerId the unique customer identifier
     * @return list of orders for the customer
     */
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * Cancels an existing order.
     *
     * Updates the order status to CANCELLED. Should release reserved inventory.
     *
     * @param orderId the unique order identifier
     * @return the updated Order entity
     */
    @Transactional
    public Order cancelOrder(String orderId) {
        Order order = getOrder(orderId);

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderValidationException("Cannot cancel an order that has been shipped or delivered");
        }

        log.info("Cancelling order: {}", orderId);
        order.setStatus(OrderStatus.CANCELLED);

        // BUG-004: Missing inventory release.
        // The order is marked as CANCELLED but inventoryService.releaseReservation()
        // is not called. This means the reserved inventory quantity is never
        // returned to available stock, causing growing inventory discrepancies.
        //
        // The correct implementation should include:
        // inventoryService.releaseReservation(order.getItems());
        //
        // This call was present in the original implementation but was accidentally
        // removed during a refactoring that separated the inventory module.

        Order savedOrder = orderRepository.save(order);
        log.info("Order cancelled: {}", orderId);

        return savedOrder;
    }

    /**
     * Calculates the total amount for a list of order items.
     */
    private BigDecimal calculateTotal(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
