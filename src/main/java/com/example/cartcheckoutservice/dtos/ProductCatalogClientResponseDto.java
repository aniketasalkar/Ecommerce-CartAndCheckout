package com.example.cartcheckoutservice.dtos;

import lombok.Data;

@Data
public class ProductCatalogClientResponseDto {
    private String name;
    private String description;
    private double price;
}
