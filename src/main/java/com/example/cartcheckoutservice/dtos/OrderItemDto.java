package com.example.cartcheckoutservice.dtos;

import lombok.Data;

@Data
public class OrderItemDto {
    private long productId;

    private int quantity;

    private double price;

    private ProductSnapshot productSnapshot;
}
