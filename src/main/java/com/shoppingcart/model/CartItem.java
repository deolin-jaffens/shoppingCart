package com.shoppingcart.model;

public class CartItem {
    private String productId;
    private int quantity;
    private double price;

    public CartItem(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = 0.0;
    }

    public CartItem(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
} 