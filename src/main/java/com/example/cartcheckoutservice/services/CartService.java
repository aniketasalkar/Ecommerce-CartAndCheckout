package com.example.cartcheckoutservice.services;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService implements ICartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    UserAuthClient userAuthClient;

    @Autowired
    ProductCatalogClient productCatalogClient;

    @Transactional
    @Override
    public Cart addProduct(String email, Long productId, Integer quantity, double price, TokensDto tokensDto) {
//        Boolean tokenStatus = userAuthClient.validateToken(email, tokensDto);

//        if (!tokenStatus) {
//            throw new InvalidSessionException("Invalid session. Login using credentials.");
//        }

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

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeProduct(String email, Long productId, Integer quantity, TokensDto tokensDto) {

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
        Cart cart = cartRepository.findByEmail(email).orElseThrow(() ->
                new NoItemsInCartException("No Items to retrieve from Cart"));

        return cart;
    }
}
