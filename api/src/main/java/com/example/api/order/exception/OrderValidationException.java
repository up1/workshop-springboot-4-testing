package com.example.api.order.exception;

/** Thrown when an order request fails input or business validation rules. */
public class OrderValidationException extends RuntimeException {

    public OrderValidationException(String message) {
        super(message);
    }
}
