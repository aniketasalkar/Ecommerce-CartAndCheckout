package com.example.cartcheckoutservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CartRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Min(value = 1, message = "product Id should be positive")
    private Long productId;

    @NotBlank
    @Min(value = 1, message = "Quantity should be atleast 1")
    @Max(value = 10, message = "Quantity should not be greater than 10")
    private Integer quantity;

    @NotBlank
    @Min(value = 0, message = "Price of the product should be positive")
    private Double productPrice;
}
