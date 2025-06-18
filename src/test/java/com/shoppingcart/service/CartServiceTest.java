package com.shoppingcart.service;

import com.shoppingcart.exception.ProductNotFoundException;
import com.shoppingcart.exception.OutOfStockException;
import com.shoppingcart.exception.InvalidQuantityException;
import com.shoppingcart.exception.*;
import com.shoppingcart.model.Cart;
import com.shoppingcart.model.CartItem;
import com.shoppingcart.model.Product;
import com.shoppingcart.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private static final String USER_ID = "user123";
    private static final String PRODUCT_ID = "prod123";
    private static final int QUANTITY = 2;

    private Product testProduct;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testProduct = new Product(PRODUCT_ID, "Test Product", 10.99, 5);
        testCart = new Cart(USER_ID);
    }

    @Test
    void addItemToCart_WhenProductExistsAndInStock_ShouldAddItem() {
        // Arrange
        when(productService.getProduct(PRODUCT_ID)).thenReturn(testProduct);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testCart));

        // Act
        cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY);

        // Assert
        verify(cartRepository).save(any(Cart.class));
        assertEquals(1, testCart.getItems().size());
        assertEquals(QUANTITY, testCart.getItems().get(0).getQuantity());
    }

    @Test
    void addItemToCart_WhenProductAlreadyInCart_ShouldIncreaseQuantity() {
        // Arrange
        CartItem existingItem = new CartItem(PRODUCT_ID, 1);
        testCart.addItem(existingItem);
        when(productService.getProduct(PRODUCT_ID)).thenReturn(testProduct);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testCart));

        // Act
        cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY);

        // Assert
        verify(cartRepository).save(any(Cart.class));
        assertEquals(1, testCart.getItems().size());
        assertEquals(QUANTITY + 1, testCart.getItems().get(0).getQuantity());
    }

    @Test
    void addItemToCart_WhenQuantityIsZero_ShouldThrowInvalidQuantityException() {
        // Arrange
        int invalidQuantity = 0;

        // Act & Assert
        assertThrows(InvalidQuantityException.class, () ->
            cartService.addItemToCart(USER_ID, PRODUCT_ID, invalidQuantity)
        );
    }

    @Test
    void addItemToCart_WhenQuantityIsNegative_ShouldThrowInvalidQuantityException() {
        // Arrange
        int invalidQuantity = -1;

        // Act & Assert
        assertThrows(InvalidQuantityException.class, () ->
            cartService.addItemToCart(USER_ID, PRODUCT_ID, invalidQuantity)
        );
    }

    @Test
    void addItemToCart_WhenProductDoesNotExist_ShouldThrowProductNotFoundException() {
        // Arrange
        when(productService.getProduct(PRODUCT_ID)).thenThrow(new ProductNotFoundException("Product not found"));

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () ->
            cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY)
        );
    }

    @Test
    void addItemToCart_WhenProductOutOfStock_ShouldThrowOutOfStockException() {
        // Arrange
        Product outOfStockProduct = new Product(PRODUCT_ID, "Test Product", 10.99, 0);
        when(productService.getProduct(PRODUCT_ID)).thenReturn(outOfStockProduct);

        // Act & Assert
        assertThrows(OutOfStockException.class, () ->
            cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY)
        );
    }

    @Test
    void addItemToCart_WhenCartDoesNotExist_ShouldCreateNewCart() {
        // Arrange
        when(productService.getProduct(PRODUCT_ID)).thenReturn(testProduct);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY);

        // Assert
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItemToCart_WhenUserIdIsNull_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            cartService.addItemToCart(null, PRODUCT_ID, QUANTITY)
        );
    }

    @Test
    void addItemToCart_WhenProductIdIsNull_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            cartService.addItemToCart(USER_ID, null, QUANTITY)
        );
    }

    @Test
    void addItemToCart_WhenUserIdIsEmpty_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            cartService.addItemToCart("", PRODUCT_ID, QUANTITY)
        );
    }

    @Test
    void addItemToCart_WhenProductIdIsEmpty_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            cartService.addItemToCart(USER_ID, "", QUANTITY)
        );
    }

    @Test
    void addItemToCart_WhenQuantityCausesIntegerOverflow_ShouldThrowArithmeticException() {
        // Arrange
        CartItem existingItem = new CartItem(PRODUCT_ID, Integer.MAX_VALUE - 1);
        testCart.addItem(existingItem);
        when(productService.getProduct(PRODUCT_ID)).thenReturn(testProduct);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(ArithmeticException.class, () ->
            cartService.addItemToCart(USER_ID, PRODUCT_ID, 2)
        );
    }

    @Test
    void addItemToCart_WhenDatabaseTransactionFails_ShouldThrowCartPersistenceException() {
        // Arrange
        when(productService.getProduct(PRODUCT_ID)).thenReturn(testProduct);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(CartPersistenceException.class, () ->
            cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY)
        );
    }

    @Test
    void addItemToCart_WhenCartReachesItemLimit_ShouldThrowCartFullException() {
        // Arrange
        when(productService.getProduct(PRODUCT_ID)).thenReturn(testProduct);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testCart));

        // Fill cart to limit
        for (int i = 0; i < Cart.MAX_ITEMS; i++) {
            testCart.addItem(new CartItem("prod" + i, 1));
        }

        // Act & Assert
        assertThrows(CartFullException.class, () ->
            cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY)
        );
    }

    @Test
    void addItemToCart_WhenProductIsInactive_ShouldThrowProductNotAvailableException() {
        // Arrange
        Product inactiveProduct = new Product(PRODUCT_ID, "Test Product", 10.99, 5);
        inactiveProduct.setActive(false);
        when(productService.getProduct(PRODUCT_ID)).thenReturn(inactiveProduct);

        // Act & Assert
        assertThrows(ProductNotAvailableException.class, () ->
            cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY)
        );
    }

    @Test
    void addItemToCart_WhenRepositoryConnectionFails_ShouldThrowCartServiceException() {
        // Arrange
        when(productService.getProduct(PRODUCT_ID)).thenReturn(testProduct);
        when(cartRepository.findByUserId(USER_ID)).thenThrow(new RuntimeException("Connection failed"));

        // Act & Assert
        assertThrows(CartServiceException.class, () ->
            cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY)
        );
    }

    @Test
    void addItemToCart_WhenProductPriceChanges_ShouldUseOriginalPrice() {
        // Arrange
        Product originalProduct = new Product(PRODUCT_ID, "Test Product", 10.99, 5);
        Product updatedProduct = new Product(PRODUCT_ID, "Test Product", 15.99, 5);
        
        when(productService.getProduct(PRODUCT_ID))
            .thenReturn(originalProduct)
            .thenReturn(updatedProduct);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testCart));

        // Act
        cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY);

        // Assert
        verify(cartRepository).save(any(Cart.class));
        assertEquals(1, testCart.getItems().size());
        assertEquals(10.99, testCart.getItems().get(0).getPrice());
    }

    @Test
    void addItemToCart_WhenConcurrentModification_ShouldMaintainConsistency() {
        // Arrange
        when(productService.getProduct(PRODUCT_ID)).thenReturn(testProduct);
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(testCart));
        
        // Simulate concurrent modification by throwing OptimisticLockingFailureException
        when(cartRepository.save(any(Cart.class)))
            .thenThrow(new OptimisticLockingFailureException("Concurrent modification"))
            .thenAnswer(i -> i.getArgument(0));

        // Act & Assert
        assertDoesNotThrow(() -> cartService.addItemToCart(USER_ID, PRODUCT_ID, QUANTITY));
        verify(cartRepository, times(2)).save(any(Cart.class));
    }
} 