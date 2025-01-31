package com.example.cartcheckoutservice.utils;

import com.example.cartcheckoutservice.dtos.CartResponseDto;
import com.example.cartcheckoutservice.dtos.TokensDto;
import com.example.cartcheckoutservice.models.Cart;

public interface IDtoMapper {
    TokensDto toTokensDto();
    CartResponseDto toCart(Cart cart);
}
