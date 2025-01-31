package com.example.cartcheckoutservice.dtos;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderRequestDto {
    private long userId;

    private List<OrderItemDto> orderItems;

    private double totalAmount;

    private String paymentMethod;

    private Date expectedDeliveryDate;

    private DeliverySnapshot deliverySnapshot;
}
