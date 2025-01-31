package com.example.cartcheckoutservice.utils;

import com.example.cartcheckoutservice.clients.UserAuthClient;
import com.example.cartcheckoutservice.dtos.TokensDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateToken {

    @Autowired
    UserAuthClient userAuthClient;

    public Boolean validateTokens(String email, TokensDto tokensDto) {
        log.info("Access Token: " + tokensDto.getAccessToken());
        log.info("Refresh Token: " + tokensDto.getRefreshToken());

        return userAuthClient.validateToken(email, tokensDto);
    }
}
