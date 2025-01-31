package com.example.cartcheckoutservice.services;

import com.example.cartcheckoutservice.dtos.OrderResponsePaymentLinkDto;
import com.example.cartcheckoutservice.dtos.PaymentMethod;
import com.example.cartcheckoutservice.dtos.TokensDto;
import com.example.cartcheckoutservice.models.Cart;

public interface ICartService {
    Cart addProduct(String email, Long productId, Integer quantity, double price, TokensDto tokensDto);
    Cart removeProduct(String email, Long productId, Integer quantity, TokensDto tokensDto);
    Cart getCart(String email, TokensDto tokensDto);
    OrderResponsePaymentLinkDto checkOutCart(String email, PaymentMethod paymentMethod, TokensDto tokensDto);
}
