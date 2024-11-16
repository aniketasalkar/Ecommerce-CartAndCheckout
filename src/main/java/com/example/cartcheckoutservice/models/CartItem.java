package com.example.cartcheckoutservice.models;

import lombok.Data;

@Data
public class CartItem {
    private Long productId;
    private Integer quantity;
    private Double price;
}
