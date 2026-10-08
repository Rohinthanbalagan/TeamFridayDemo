package com.company.ordermanagement.repository;

import com.company.ordermanagement.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Customer entity persistence operations.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    List<Customer> findByStatus(String status);

    List<Customer> findByEmail(String email);
}
