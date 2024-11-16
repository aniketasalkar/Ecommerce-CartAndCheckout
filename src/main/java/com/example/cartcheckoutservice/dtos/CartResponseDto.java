package com.example.cartcheckoutservice.dtos;

import com.example.cartcheckoutservice.models.CartItem;
import com.example.cartcheckoutservice.models.RequestStatus;
import lombok.Data;

import java.util.List;

@Data
public class CartResponseDto {
    private List<CartItem> cartItems;
    private Double cartTotal;
    private RequestStatus requestStatus;
    private String id;
    private String email;
}
