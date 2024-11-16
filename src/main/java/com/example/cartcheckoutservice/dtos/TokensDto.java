package com.example.cartcheckoutservice.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TokensDto {
    @NotEmpty(message = "AccessToken Required")
    private String accessToken;

    @NotEmpty(message = "RefreshToken Required")
    private String refreshToken;
}
