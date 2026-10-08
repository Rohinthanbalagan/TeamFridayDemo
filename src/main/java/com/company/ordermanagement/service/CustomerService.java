package com.company.ordermanagement.service;

import com.company.ordermanagement.exception.CustomerNotFoundException;
import com.company.ordermanagement.model.Address;
import com.company.ordermanagement.model.Customer;
import com.company.ordermanagement.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service responsible for customer-related business operations.
 * Handles customer eligibility validation, profile lookups, and address retrieval.
 */
@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Validates whether a customer is eligible to place orders.
     * Checks customer status and account balance.
     *
     * @param customerId the unique customer identifier
     * @return true if the customer is eligible, false otherwise
     * @throws CustomerNotFoundException if the customer does not exist
     */
    public boolean isCustomerEligible(String customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));

        // BUG-001: This validation is flawed — it only allows PREMIUM customers.
        // Standard ACTIVE customers are incorrectly rejected because the condition
        // checks for equality to "PREMIUM" instead of checking against a deny-list
        // of invalid statuses (SUSPENDED, CLOSED, BANNED).
        // The correct approach would be to deny specific statuses rather than
        // requiring a single allowed status.
        if (!"PREMIUM".equals(customer.getStatus())) {
            log.warn("Customer {} has ineligible status: {}", customerId, customer.getStatus());
            return false;
        }

        return customer.getAccountBalance() >= 0;
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId the unique customer identifier
     * @return the Customer entity
     * @throws CustomerNotFoundException if the customer does not exist
     */
    public Customer getCustomer(String customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    /**
     * Retrieves the shipping address for a customer.
     * Note: Customers who registered via mobile app may not have an address on file.
     *
     * @param customerId the unique customer identifier
     * @return the customer's address, which may be null
     * @throws CustomerNotFoundException if the customer does not exist
     */
    public Address getCustomerAddress(String customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));

        // Returns customer.getAddress() directly without null check.
        // This is related to BUG-002: callers that dereference the return value
        // without null-checking will get a NullPointerException.
        return customer.getAddress();
    }
}
