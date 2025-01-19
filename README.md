# Ecommerce-CartAndCheckout

Cart and CheckOut service for Ecommerce Application

## API Documentation

### CartController

#### Add to Cart
- **Endpoint:** `/api/cart/addItem`
- **Method:** `POST`
- **Description:** Adds an item to the cart.
- **Request Body:** `CartRequestDto`
- **Response:**
  - `201 Created`: Returns the cart details with the added item.
  - `400 Bad Request`: If the request is invalid.
  - `500 Internal Server Error`: If there is an error during the addition.

#### Get Cart
- **Endpoint:** `/api/cart/getCart/{email}`
- **Method:** `GET`
- **Description:** Retrieves the cart for a specific user.
- **Path Variable:** `email`
- **Response:**
  - `200 OK`: Returns the cart details.
  - `404 Not Found`: If the cart is not found.
  - `500 Internal Server Error`: If there is an error during retrieval.

#### Remove from Cart
- **Endpoint:** `/api/cart/removeItem`
- **Method:** `DELETE`
- **Description:** Removes an item from the cart.
- **Request Body:** `RemoveFromCartRequestDto`
- **Response:**
  - `200 OK`: Returns the updated cart details.
  - `400 Bad Request`: If the request is invalid.
  - `500 Internal Server Error`: If there is an error during the removal.

#### Checkout
- **Endpoint:** `/api/cart/checkout/{email}`
- **Method:** `GET`
- **Description:** Proceeds to checkout for a specific user's cart.
- **Path Variable:** `email`
- **Response:**
  - `200 OK`: Returns a success message.
  - `500 Internal Server Error`: If there is an error during checkout.

### UserAuthClient

#### Validate Token
- **Endpoint:** `/api/auth/{email}/validateToken`
- **Method:** `POST`
- **Description:** Validates the token for a specific user.
- **Path Variable:** `email`
- **Request Body:** `TokensDto`
- **Response:**
  - `200 OK`: If the token is valid.
  - `400 Bad Request`: If the request is invalid.
  - `500 Internal Server Error`: If there is an error during validation.

### ProductCatalogClient

#### Get Product
- **Endpoint:** `/api/productCategoryService/products/{id}`
- **Method:** `GET`
- **Description:** Retrieves a product by ID.
- **Path Variable:** `id`
- **Response:**
  - `200 OK`: Returns the product details.
  - `404 Not Found`: If the product is not found.
  - `500 Internal Server Error`: If there is an error during retrieval.
