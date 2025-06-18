package com.shoppingcart.controller;

import com.shoppingcart.model.Product;
import com.shoppingcart.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable String productId) {
        Product product = productService.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllActiveProducts() {
        List<Product> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{productId}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable String productId,
            @RequestParam int quantity) {
        productService.updateStock(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable String productId,
            @RequestParam int quantity) {
        boolean isAvailable = productService.isProductAvailable(productId, quantity);
        return ResponseEntity.ok(isAvailable);
    }
} 