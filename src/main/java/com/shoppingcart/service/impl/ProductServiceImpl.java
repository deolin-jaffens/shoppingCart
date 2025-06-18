package com.shoppingcart.service.impl;

import com.shoppingcart.exception.ProductNotFoundException;
import com.shoppingcart.model.Product;
import com.shoppingcart.repository.ProductRepository;
import com.shoppingcart.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product getProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    @Override
    @Transactional
    public void updateStock(String productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        Product product = getProduct(productId);
        product.setStockQuantity(quantity);
        productRepository.save(product);
    }

    @Override
    public boolean isProductAvailable(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Product product = getProduct(productId);
        return product.isActive() && product.getStockQuantity() >= quantity;
    }
} 