package com.example.cartcheckoutservice.utils;

import com.example.cartcheckoutservice.dtos.CartResponseDto;
import com.example.cartcheckoutservice.dtos.TokensDto;
import com.example.cartcheckoutservice.models.Cart;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper implements IDtoMapper {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public TokensDto toTokensDto() {
        TokensDto tokensDto = new TokensDto();
        tokensDto.setAccessToken(httpServletRequest.getHeaders(HttpHeaders.SET_COOKIE).toString());
        tokensDto.setRefreshToken(httpServletRequest.getHeaders(HttpHeaders.SET_COOKIE2).toString());

        return tokensDto;
    }

    @Override
    public CartResponseDto toCart(Cart cart) {
        CartResponseDto cartResponseDto = new CartResponseDto();
        cartResponseDto.setCartTotal(cart.getCartTotalPrice());
        cartResponseDto.setCartItems(cart.getCartItems());
        cartResponseDto.setUuid(cart.getUuid());
        cartResponseDto.setEmail(cart.getEmail());

        return cartResponseDto;
    }
}
