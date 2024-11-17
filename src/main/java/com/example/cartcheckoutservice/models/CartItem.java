package com.example.cartcheckoutservice.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartItem implements Serializable {
    private Long productId;
    private Integer quantity;
    private Double price;
}
