# Intentionally Introduced Bugs

This document catalogs the intentional bugs introduced into the Order Management Service for AI agent investigation testing.

> **⚠️ These bugs are deliberate.** They simulate realistic production defects that an AI RCA agent should be able to identify.

---

## BUG-001: ACTIVE customer incorrectly rejected during order creation

**File:** `CustomerService.java`  
**Method:** `isCustomerEligible()`  
**Lines:** ~38-44  
**Severity:** P1  

**Symptom:** Customers with status `ACTIVE` receive error "Customer is not eligible to place orders" when trying to create an order.

**Root Cause:** The eligibility check uses an allow-list approach, only permitting customers with status `PREMIUM`. The condition `!"PREMIUM".equals(customer.getStatus())` rejects all non-PREMIUM customers, including `ACTIVE`, `VIP`, etc.

**Correct Approach:** Use a deny-list checking for `SUSPENDED`, `CLOSED`, `BANNED` statuses.

**Related AYS Incident:** AYS-1001

---

## BUG-002: Null customer address causes NullPointerException

**File:** `OrderService.java`  
**Method:** `createOrder()`  
**Lines:** ~75-80  
**Severity:** P2  

**Symptom:** Order creation throws `NullPointerException` for customers who registered through the mobile app without providing a shipping address.

**Root Cause:** `CustomerService.getCustomerAddress()` returns `null` when the customer has no address on file. `OrderService.createOrder()` calls `shippingAddress.getStreet()` without null-checking the address, causing an NPE.

**Correct Approach:** Add null check before accessing address properties. Throw a descriptive `OrderValidationException` if address is required but missing.

---

## BUG-003: Duplicate orders created due to missing idempotency validation

**File:** `OrderService.java`  
**Method:** `createOrder()`  
**Lines:** ~52-65  
**Also:** `OrderController.java` (no `X-Idempotency-Key` header handling)  
**Severity:** P1  

**Symptom:** When a client retries a request (due to timeout or network issues), duplicate orders are created for the same customer and items.

**Root Cause:** No idempotency mechanism exists. Each `POST /api/orders` request creates a new order regardless of whether it is a retry. There is no idempotency key, no duplicate detection, and no database constraint preventing duplicates.

**Correct Approach:** Accept an `X-Idempotency-Key` header, check for existing orders with the same key before creating, and add a unique database constraint.

**Related AYS Incident:** AYS-1003

---

## BUG-004: Inventory not released after order cancellation

**File:** `OrderService.java`  
**Method:** `cancelOrder()`  
**Lines:** ~107-120  
**Severity:** P2  

**Symptom:** After cancelling an order, the reserved inventory is not returned to available stock. System inventory counts become increasingly inaccurate over time.

**Root Cause:** `cancelOrder()` updates the order status to `CANCELLED` but does not call `inventoryService.releaseReservation(order.getItems())`. This call was present in the original implementation but was accidentally removed during a refactoring.

**Correct Approach:** Add `inventoryService.releaseReservation(order.getItems())` call before saving the cancelled order. Implement compensating transaction pattern.

**Related AYS Incident:** AYS-1004
