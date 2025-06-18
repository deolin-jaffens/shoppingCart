package com.shoppingcart.service.impl;

import com.shoppingcart.exception.*;
import com.shoppingcart.model.Cart;
import com.shoppingcart.model.CartItem;
import com.shoppingcart.model.Product;
import com.shoppingcart.repository.CartRepository;
import com.shoppingcart.service.CartService;
import com.shoppingcart.service.ProductService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;

    public CartServiceImpl(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    @Override
    @Transactional
    public void addItemToCart(String userId, String productId, int quantity) {
        // Validate input parameters
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero");
        }

        try {
            Product product = productService.getProduct(productId);
            if (product == null) {
                throw new ProductNotFoundException("Product not found");
            }

            if (!product.isActive()) {
                throw new ProductNotAvailableException("Product is not available");
            }

            if (product.getStockQuantity() < quantity) {
                throw new OutOfStockException("Product is out of stock");
            }

            Cart cart = cartRepository.findByUserId(userId)
                    .orElse(new Cart(userId));

            Optional<CartItem> existingItem = cart.getItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst();

            if (existingItem.isPresent()) {
                int newQuantity = existingItem.get().getQuantity() + quantity;
                if (newQuantity < 0) { // Check for integer overflow
                    throw new ArithmeticException("Quantity would exceed maximum allowed value");
                }
                existingItem.get().setQuantity(newQuantity);
            } else {
                cart.addItem(new CartItem(productId, quantity, product.getPrice()));
            }

            try {
                cartRepository.save(cart);
            } catch (OptimisticLockingFailureException e) {
                // Retry once on concurrent modification
                cart = cartRepository.findByUserId(userId)
                        .orElseThrow(() -> new CartServiceException("Cart not found on retry"));
                cartRepository.save(cart);
            } catch (RuntimeException e) {
                throw new CartPersistenceException("Failed to save cart", e);
            }
        } catch (RuntimeException e) {
            if (e instanceof CartServiceException || 
                e instanceof ProductNotFoundException || 
                e instanceof OutOfStockException || 
                e instanceof InvalidQuantityException || 
                e instanceof ProductNotAvailableException ||
                e instanceof ArithmeticException ||
                e instanceof CartFullException ||
                e instanceof CartPersistenceException) {
                throw e;
            }
            throw new CartServiceException("Failed to add item to cart", e);
        }
    }

    @Override
    public Cart getCart(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + userId));
    }

    @Override
    @Transactional
    public void removeItemFromCart(String userId, String productId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        
        try {
            cartRepository.save(cart);
        } catch (RuntimeException e) {
            throw new CartPersistenceException("Failed to remove item from cart", e);
        }
    }
} 