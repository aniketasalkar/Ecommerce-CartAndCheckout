package com.example.cartcheckoutservice.exceptions;

public class NoItemsInCartException extends RuntimeException {
    public NoItemsInCartException(String message) {
        super(message);
    }
}
