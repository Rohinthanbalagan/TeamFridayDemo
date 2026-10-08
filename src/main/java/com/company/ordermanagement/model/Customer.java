package com.company.ordermanagement.model;

import jakarta.persistence.*;

/**
 * Customer entity representing a customer in the system.
 * Customer records are loaded from external customer service.
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String status;  // ACTIVE, PREMIUM, VIP, SUSPENDED, CLOSED, BANNED

    private double accountBalance;

    @Embedded
    private Address address;  // May be null for customers registered via mobile app

    // Constructors
    public Customer() {}

    public Customer(String id, String name, String email, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.accountBalance = 0.0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}
