package com.example.cartcheckoutservice.controllers;

import com.example.cartcheckoutservice.dtos.*;
import com.example.cartcheckoutservice.models.Cart;
import com.example.cartcheckoutservice.models.RequestStatus;
import com.example.cartcheckoutservice.services.ICartService;
import com.example.cartcheckoutservice.utils.IDtoMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    @Autowired
    IDtoMapper dtoMapper;

    @PostMapping("/addItem")
    public ResponseEntity<CartResponseDto> addToCart(@RequestBody CartRequestDto requestDto) {
        try {
            Cart cart = cartService.addProduct(requestDto.getEmail(), requestDto.getProductId(), requestDto.getQuantity(),
                    requestDto.getProductPrice(), dtoMapper.toTokensDto());
            CartResponseDto responseDto = dtoMapper.toCart(cart);
            responseDto.setRequestStatus(RequestStatus.SUCCESS);

            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (Exception exception) {
            throw exception;
        }
    }

    @GetMapping("/getCart/{email}")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable String email) {
        try {
            Cart cart = cartService.getCart(email, dtoMapper.toTokensDto());
            CartResponseDto responseDto = dtoMapper.toCart(cart);
            responseDto.setRequestStatus(RequestStatus.SUCCESS);

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }

    @DeleteMapping("/removeItem")
    public ResponseEntity<CartResponseDto> removeFromCart(@RequestBody RemoveFromCartRequestDto removeFromCartRequestDto) {
        try {
            Cart cart = cartService.removeProduct(removeFromCartRequestDto.getEmail(), removeFromCartRequestDto.getProductId(),
                    removeFromCartRequestDto.getQuantity(), dtoMapper.toTokensDto());
            CartResponseDto responseDto = dtoMapper.toCart(cart);
            responseDto.setRequestStatus(RequestStatus.SUCCESS);

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }

    // To be Implemented
    @PostMapping("/checkout/{email}")
    public ResponseEntity<OrderResponsePaymentLinkDto> checkout(@PathVariable String email, @RequestBody CartCheckoutDto cartCheckoutDto) {
        try {
            OrderResponsePaymentLinkDto orderResponsePaymentLinkDto = cartService.checkOutCart(email,
                    cartCheckoutDto.getPaymentMethod(), dtoMapper.toTokensDto());

            return new ResponseEntity<>(orderResponsePaymentLinkDto, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }
}
