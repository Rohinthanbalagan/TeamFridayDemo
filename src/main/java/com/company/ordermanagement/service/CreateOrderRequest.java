package com.company.ordermanagement.service;

import com.company.ordermanagement.model.OrderItem;
import java.util.List;

/**
 * Request object for creating a new order.
 */
public class CreateOrderRequest {

    private String customerId;
    private List<OrderItem> items;

    public CreateOrderRequest() {}

    public CreateOrderRequest(String customerId, List<OrderItem> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
