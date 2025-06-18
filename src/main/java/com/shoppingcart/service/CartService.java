package com.shoppingcart.service;

import com.shoppingcart.exception.*;
import com.shoppingcart.model.Cart;

public interface CartService {
    /**
     * Adds an item to the user's shopping cart
     *
     * @param userId The ID of the user
     * @param productId The ID of the product to add
     * @param quantity The quantity of the product to add
     * @throws IllegalArgumentException if userId or productId is null/empty
     * @throws InvalidQuantityException if quantity is less than or equal to zero
     * @throws ProductNotFoundException if the product doesn't exist
     * @throws ProductNotAvailableException if the product is not active
     * @throws OutOfStockException if there's insufficient stock
     * @throws CartServiceException if there's an error processing the request
     * @throws CartPersistenceException if there's an error saving the cart
     */
    void addItemToCart(String userId, String productId, int quantity);

    /**
     * Retrieves a user's shopping cart
     *
     * @param userId The ID of the user
     * @return The user's shopping cart
     * @throws CartNotFoundException if the cart doesn't exist
     */
    Cart getCart(String userId);

    /**
     * Removes an item from the user's shopping cart
     *
     * @param userId The ID of the user
     * @param productId The ID of the product to remove
     * @throws CartNotFoundException if the cart doesn't exist
     * @throws CartServiceException if there's an error processing the request
     */
    void removeItemFromCart(String userId, String productId);
} 