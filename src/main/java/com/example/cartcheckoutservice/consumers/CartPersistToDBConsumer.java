package com.example.cartcheckoutservice.consumers;

import com.example.cartcheckoutservice.models.Cart;
import com.example.cartcheckoutservice.services.CartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CartPersistToDBConsumer {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CartService cartService;

    private static final Logger logger = LoggerFactory.getLogger(CartPersistToDBConsumer.class);

    @KafkaListener(topics = "add-to-cart", groupId = "CartPersistToDB")
    public void PersistToDb(String cartData) {
        Cart cart;
        try {
            cart = objectMapper.readValue(cartData, Cart.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        logger.info("Received kafka Event for : {}", cart);
        Cart savedCart = cartService.persistCartData(cart);
        logger.info("Saved cart : {}", savedCart);
    }
}
