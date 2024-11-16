package com.example.cartcheckoutservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RemoveFromCartRequestDto {
    @NotBlank
    @Min(value = 1, message = "product Id should be positive")
    private Long productId;

    @NotBlank
    @Min(value = 1, message = "Quantity should be atleast 1")
    @Max(value = 10, message = "Quantity should not be greater than 10")
    private Integer quantity;

    @NotBlank
    @Email
    private String email;
}
