package com.shoppingcart.controller;

import com.shoppingcart.model.Cart;
import com.shoppingcart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addItemToCart(
            @RequestParam String userId,
            @RequestParam String productId,
            @RequestParam int quantity) {
        cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable String userId) {
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<Void> removeItemFromCart(
            @PathVariable String userId,
            @PathVariable String productId) {
        cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok().build();
    }
} 