package com.example.cartcheckoutservice.services;

import com.example.cartcheckoutservice.clients.*;
import com.example.cartcheckoutservice.dtos.*;
import com.example.cartcheckoutservice.exceptions.BadRequestException;
import com.example.cartcheckoutservice.exceptions.InvalidProductException;
import com.example.cartcheckoutservice.exceptions.InvalidSessionException;
import com.example.cartcheckoutservice.exceptions.NoItemsInCartException;
import com.example.cartcheckoutservice.models.Cart;
import com.example.cartcheckoutservice.models.CartItem;
import com.example.cartcheckoutservice.repositories.CartRepository;
import com.example.cartcheckoutservice.utils.ValidateToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CartService implements ICartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    UserAuthClient userAuthClient;

    @Autowired
    ProductCatalogClient productCatalogClient;

    @Autowired
    UserManagementClient userManagementClient;

    @Autowired
    OrderManagementClient orderManagementClient;

    @Autowired
    ValidateToken validateToken;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    KafkaProducerClient kafkaProducerClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Transactional
    @Override
    public Cart addProduct(String email, Long productId, Integer quantity, double price, TokensDto tokensDto) {

//        if (!validateToken.validateTokens(email, tokensDto)) {
//            throw new InvalidSessionException("Invalid session. Login using credentials.");
//        }

        ProductCatalogClientResponseDto productCatalogClientResponseDto = productCatalogClient.getProduct(productId);

        if (productCatalogClientResponseDto == null) {
            throw new InvalidProductException("Invalid product id.");
        }

        boolean existInCart = false;
        Cart cart = cartRepository.findByEmail(email).orElse(new Cart());
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            CartItem cartItem = new CartItem();
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(price);

            List<CartItem> cartItems = new ArrayList<>();
            cartItems.add(cartItem);
            cart.setCartItems(cartItems);
            cart.setCartTotalPrice(quantity * price);
        } else {
            for (CartItem cartItem : cart.getCartItems()) {
                if (cartItem.getProductId().equals(productId)) {
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    cart.setCartTotalPrice(cart.getCartTotalPrice() + quantity * price);
                    existInCart = true;
                    break;
                }
            }

            if (!existInCart) {
                CartItem cartItem = new CartItem();
                cartItem.setProductId(productId);
                cartItem.setQuantity(quantity);
                cartItem.setPrice(price);
                cart.getCartItems().add(cartItem);
                cart.setCartTotalPrice(cart.getCartTotalPrice() + quantity * price);
            }
        }
        cart.setEmail(email);

        Date currentDate = new Date();
        cart.setCreatedAt(currentDate);
        cart.setUpdatedAt(currentDate);

        cart.setUuid(UUID.randomUUID().toString());

        redisTemplate.opsForHash().put("__Carts__", email, cart);
        redisTemplate.opsForHash().put("__UUIDEmailIndex__", cart.getUuid(), email);

        generatePersistCartEvent(cart);

        return cart;
    }

    private void generatePersistCartEvent(Cart cart) {
        String topic = "add-to-cart";
        try {
            kafkaProducerClient.sendMessage(topic, objectMapper.writeValueAsString(cart));
            logger.info("Sent event for "+ cart + "to topic: " + topic);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Cart persistCartData(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public Cart removeProduct(String email, Long productId, Integer quantity, TokensDto tokensDto) {

//        if (!validateToken.validateTokens(email, tokensDto)) {
//            throw new InvalidSessionException("Invalid session. Login using credentials.");
//        }

        ProductCatalogClientResponseDto productCatalogClientResponseDto = productCatalogClient.getProduct(productId);
        if (productCatalogClientResponseDto == null) {
            throw new InvalidProductException("Invalid product id.");
        }

        Cart cart = cartRepository.findByEmail(email).orElseThrow(() ->
                new NoItemsInCartException("No Items to remove from Cart"));

        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getProductId().equals(productId)) {
                if (cartItem.getQuantity() < quantity) {
                    throw new BadRequestException("Quantity is greater than actual Products.");
                } else if (cartItem.getQuantity() == quantity) {
                    cart.getCartItems().remove(cartItem);
                } else {
                    cartItem.setQuantity(cartItem.getQuantity() - quantity);
                    cart.setCartTotalPrice(cart.getCartTotalPrice() - (cartItem.getPrice() * quantity));
                }
            }
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart getCart(String email, TokensDto tokensDto) {

//        if (!validateToken.validateTokens(email, tokensDto)) {
//            throw new InvalidSessionException("Invalid session. Login using credentials.");
//        }

        Cart cart = null;
        cart = (Cart) redisTemplate.opsForHash().get("__Carts__", email);

        if (cart == null) {
            logger.info("No cart found for email: " + email + "in the cache");
            System.out.println("Not found in Cache");

            //Searching in database
            cart = cartRepository.findByEmail(email).orElseThrow(() ->
                    new NoItemsInCartException("No Items to retrieve from Cart"));

            redisTemplate.opsForHash().put("__Carts__", email, cart);
//            redisTemplate.opsForHash().put("__UUIDEmailIndex__", cart.getUuid(), email);
        }

        System.out.println(cart);

        return cart;
    }

    @Override
    public OrderResponsePaymentLinkDto checkOutCart(String email, PaymentMethod paymentMethod, TokensDto tokensDto) {

//        if (!validateToken.validateTokens(email, tokensDto)) {
//            throw new InvalidSessionException("Invalid session. Login using credentials.");
//        }
        UserResponseDto userResponseDto = userManagementClient.getUser(email).getBody();
        Cart cart = getCart(email, tokensDto);
        OrderRequestDto orderRequestDto = new OrderRequestDto();

        orderRequestDto.setUserId(userResponseDto.getId());
        orderRequestDto.setTotalAmount(cart.getCartTotalPrice());

        orderRequestDto.setOrderItems(getOrderItems(cart));
        orderRequestDto.setDeliverySnapshot(getDeliverySnapshot(userResponseDto));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 5);
        orderRequestDto.setExpectedDeliveryDate(calendar.getTime());
        orderRequestDto.setPaymentMethod(paymentMethod.toString());

        return orderManagementClient.createOrder(orderRequestDto).getBody();
    }

    private DeliverySnapshot getDeliverySnapshot(UserResponseDto userResponseDto) {
        AddressDto addressDto = userManagementClient.getDefaultAddress(userResponseDto.getId()).getBody();
        DeliverySnapshot deliverySnapshot = new DeliverySnapshot();
        deliverySnapshot.setName(addressDto.getName());
        deliverySnapshot.setStreet(addressDto.getStreet());
        deliverySnapshot.setCity(addressDto.getCity());
        deliverySnapshot.setState(addressDto.getState());
        deliverySnapshot.setZip(addressDto.getZip());
        deliverySnapshot.setPhone(userResponseDto.getPhoneNumber());

        return deliverySnapshot;
    }

    private List<OrderItemDto> getOrderItems(Cart cart) {
        List<OrderItemDto> orderItemDtos = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItemDto orderItemDto = new OrderItemDto();
            orderItemDto.setProductId(cartItem.getProductId());
            orderItemDto.setQuantity(cartItem.getQuantity());
            orderItemDto.setPrice(cartItem.getPrice());

            ProductCatalogClientResponseDto productCatalogClientResponseDto = productCatalogClient.getProduct(cartItem.getProductId());
            ProductSnapshot productSnapshot = new ProductSnapshot();
            productSnapshot.setProductName(productCatalogClientResponseDto.getName());
            productSnapshot.setDescription(productCatalogClientResponseDto.getDescription());
            productSnapshot.setPrice(productCatalogClientResponseDto.getPrice());
            productSnapshot.setImageUrl(productSnapshot.getImageUrl());

            orderItemDto.setProductSnapshot(productSnapshot);
            orderItemDtos.add(orderItemDto);
        }

        return orderItemDtos;
    }
}
