package com.company.ordermanagement.model;

import jakarta.persistence.*;

/**
 * Inventory entity tracking product stock levels and reservations.
 */
@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    private String productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int availableQuantity;

    @Column(nullable = false)
    private int reservedQuantity;

    public Inventory() {}

    public Inventory(String productId, String productName, int availableQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public int getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(int reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    /**
     * Get the effective available quantity (total minus reserved).
     */
    public int getEffectiveAvailable() {
        return availableQuantity - reservedQuantity;
    }
}
