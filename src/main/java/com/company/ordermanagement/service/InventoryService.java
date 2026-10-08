package com.company.ordermanagement.service;

import com.company.ordermanagement.model.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for inventory management operations.
 * Handles stock level queries, inventory reservation, and release.
 */
@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    // In-memory inventory tracking for demo purposes
    private final Map<String, Integer> reservedInventory = new HashMap<>();

    /**
     * Reserves inventory for the given order items.
     * In production, this would interact with the inventory database.
     *
     * @param items the list of order items to reserve
     */
    public void reserveInventory(List<OrderItem> items) {
        for (OrderItem item : items) {
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            int currentReserved = reservedInventory.getOrDefault(productId, 0);
            reservedInventory.put(productId, currentReserved + quantity);

            log.info("Reserved {} units of product {} (total reserved: {})",
                quantity, productId, reservedInventory.get(productId));
        }
    }

    /**
     * Releases previously reserved inventory.
     * Should be called when an order is cancelled.
     *
     * @param items the list of order items whose reservations should be released
     */
    public void releaseReservation(List<OrderItem> items) {
        for (OrderItem item : items) {
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            int currentReserved = reservedInventory.getOrDefault(productId, 0);
            int newReserved = Math.max(0, currentReserved - quantity);
            reservedInventory.put(productId, newReserved);

            log.info("Released {} units of product {} (total reserved: {})",
                quantity, productId, newReserved);
        }
    }

    /**
     * Checks available inventory for a product.
     *
     * @param productId the product identifier
     * @param requiredQuantity the required quantity
     * @return true if sufficient inventory is available
     */
    public boolean checkAvailability(String productId, int requiredQuantity) {
        // Demo: Always return true for simplicity
        // In production, this would query the inventory database
        int reserved = reservedInventory.getOrDefault(productId, 0);
        log.debug("Checking availability for {}: reserved={}, required={}",
            productId, reserved, requiredQuantity);
        return true;
    }

    /**
     * Gets the current reserved quantity for a product.
     *
     * @param productId the product identifier
     * @return the reserved quantity
     */
    public int getReservedQuantity(String productId) {
        return reservedInventory.getOrDefault(productId, 0);
    }
}
