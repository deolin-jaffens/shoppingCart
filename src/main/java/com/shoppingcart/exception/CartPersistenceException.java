package com.shoppingcart.exception;

public class CartPersistenceException extends RuntimeException {
    public CartPersistenceException(String message) {
        super(message);
    }

    public CartPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
} 