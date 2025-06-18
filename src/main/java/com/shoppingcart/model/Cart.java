package com.shoppingcart.model;

import com.shoppingcart.exception.CartFullException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Cart {
    public static final int MAX_ITEMS = 10;
    private String userId;
    private List<CartItem> items;

    public Cart(String userId) {
        this.userId = userId;
        this.items = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void addItem(CartItem item) {
        if (items.size() >= MAX_ITEMS) {
            throw new CartFullException("Cart has reached maximum item limit");
        }
        items.add(item);
    }
} 