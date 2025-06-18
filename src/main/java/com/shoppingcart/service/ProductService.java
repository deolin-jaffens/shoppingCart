package com.shoppingcart.service;

import com.shoppingcart.exception.ProductNotFoundException;
import com.shoppingcart.model.Product;
import java.util.List;

public interface ProductService {
    /**
     * Retrieves a product by its ID
     *
     * @param productId The ID of the product to retrieve
     * @return The product if found, null otherwise
     * @throws ProductNotFoundException if the product doesn't exist
     */
    Product getProduct(String productId);

    /**
     * Retrieves all active products
     *
     * @return List of all active products
     */
    List<Product> getAllActiveProducts();

    /**
     * Updates the stock quantity of a product
     *
     * @param productId The ID of the product to update
     * @param quantity The new quantity to set
     * @throws ProductNotFoundException if the product doesn't exist
     * @throws IllegalArgumentException if quantity is negative
     */
    void updateStock(String productId, int quantity);

    /**
     * Checks if a product is available in the requested quantity
     *
     * @param productId The ID of the product to check
     * @param quantity The quantity to check availability for
     * @return true if the product is available in the requested quantity
     * @throws ProductNotFoundException if the product doesn't exist
     */
    boolean isProductAvailable(String productId, int quantity);
} 