# Shopping Cart Application

A robust shopping cart application built with Spring Boot that provides functionality for managing shopping carts and products.

## Features

- Add items to shopping cart
- Product management
- Stock validation
- Concurrent cart operations handling
- Exception handling for various scenarios

## Technical Stack

- Java
- Spring Boot
- Spring Data JPA
- Maven

## Project Structure

```
src/main/java/com/shoppingcart/
├── controller/
├── service/
│   ├── impl/
│   │   ├── CartServiceImpl.java
│   │   └── ProductServiceImpl.java
│   ├── CartService.java
│   └── ProductService.java
├── model/
├── repository/
└── exception/
```

## Getting Started

1. Clone the repository
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

### Cart Operations
- `POST /api/cart/add` - Add item to cart
- `GET /api/cart/{userId}` - Get cart by user ID
- `DELETE /api/cart/{userId}/items/{productId}` - Remove item from cart

### Product Operations
- `GET /api/products/{productId}` - Get product details
- `GET /api/products` - List all products

## Error Handling

The application handles various exceptions:
- ProductNotFoundException
- OutOfStockException
- InvalidQuantityException
- ProductNotAvailableException
- CartServiceException
- CartPersistenceException
