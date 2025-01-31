package com.example.cartcheckoutservice.clients;

import com.example.cartcheckoutservice.dtos.OrderRequestDto;
import com.example.cartcheckoutservice.dtos.OrderResponsePaymentLinkDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ORDERMANAGEMENTSERVICE")
public interface OrderManagementClient {
    @PostMapping("/api/orders/create_order")
    ResponseEntity<OrderResponsePaymentLinkDto> createOrder(@RequestBody @Valid OrderRequestDto orderRequestDto);
}
