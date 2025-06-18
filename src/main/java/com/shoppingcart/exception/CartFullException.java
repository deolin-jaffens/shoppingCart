package com.shoppingcart.exception;

public class CartFullException extends RuntimeException {
    public CartFullException(String message) {
        super(message);
    }
} 