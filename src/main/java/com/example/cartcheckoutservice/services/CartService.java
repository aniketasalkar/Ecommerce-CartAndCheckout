package com.example.cartcheckoutservice.services;

import com.example.cartcheckoutservice.clients.KafkaProducerClient;
import com.example.cartcheckoutservice.clients.ProductCatalogClient;
import com.example.cartcheckoutservice.clients.UserAuthClient;
import com.example.cartcheckoutservice.dtos.ProductCatalogClientResponseDto;
import com.example.cartcheckoutservice.dtos.TokensDto;
import com.example.cartcheckoutservice.exceptions.BadRequestException;
import com.example.cartcheckoutservice.exceptions.InvalidProductException;
import com.example.cartcheckoutservice.exceptions.InvalidSessionException;
import com.example.cartcheckoutservice.exceptions.NoItemsInCartException;
import com.example.cartcheckoutservice.models.Cart;
import com.example.cartcheckoutservice.models.CartItem;
import com.example.cartcheckoutservice.repositories.CartRepository;
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
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    KafkaProducerClient kafkaProducerClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Transactional
    @Override
    public Cart addProduct(String email, Long productId, Integer quantity, double price, TokensDto tokensDto) {

        if (!validateTokens(email, tokensDto)) {
            throw new InvalidSessionException("Invalid session. Login using credentials.");
        }

        ProductCatalogClientResponseDto productCatalogClientResponseDto = productCatalogClient.getProduct(productId);

        if (productCatalogClientResponseDto == null) {
            throw new InvalidProductException("Invalid product id.");
        }

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
                }
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

        if (!validateTokens(email, tokensDto)) {
            throw new InvalidSessionException("Invalid session. Login using credentials.");
        }

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

        if (!validateTokens(email, tokensDto)) {
            throw new InvalidSessionException("Invalid session. Login using credentials.");
        }

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

    private Boolean validateTokens(String email, TokensDto tokensDto) {
        logger.info("Access Token: " + tokensDto.getAccessToken());
        logger.info("Refresh Token: " + tokensDto.getRefreshToken());

        return userAuthClient.validateToken(email, tokensDto);
    }
}
