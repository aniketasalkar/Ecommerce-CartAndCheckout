package com.example.cartcheckoutservice.services;

import com.example.cartcheckoutservice.dtos.TokensDto;
import com.example.cartcheckoutservice.models.Cart;

import java.util.Optional;

public interface ICartService {
    Cart addProduct(String email, Long productId, Integer quantity, double price, TokensDto tokensDto);
    Cart removeProduct(String email, Long productId, Integer quantity, TokensDto tokensDto);
    Cart getCart(String email, TokensDto tokensDto);
}
