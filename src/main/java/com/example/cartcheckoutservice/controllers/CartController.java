package com.example.cartcheckoutservice.controllers;

import com.example.cartcheckoutservice.dtos.CartRequestDto;
import com.example.cartcheckoutservice.dtos.CartResponseDto;
import com.example.cartcheckoutservice.dtos.RemoveFromCartRequestDto;
import com.example.cartcheckoutservice.dtos.TokensDto;
import com.example.cartcheckoutservice.models.Cart;
import com.example.cartcheckoutservice.models.RequestStatus;
import com.example.cartcheckoutservice.services.ICartService;
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
    private HttpServletRequest httpServletRequest;

    @PostMapping("/addItem")
    public ResponseEntity<CartResponseDto> addToCart(@RequestBody CartRequestDto requestDto) {
        try {
            TokensDto tokensDto = new TokensDto();
            tokensDto.setAccessToken(httpServletRequest.getHeader("Set-Cookie").toString());
            tokensDto.setRefreshToken(httpServletRequest.getHeader("Set-Cookie2").toString());
            Cart cart = cartService.addProduct(requestDto.getEmail(), requestDto.getProductId(), requestDto.getQuantity(),
                    requestDto.getProductPrice(), tokensDto);
            CartResponseDto responseDto = fromCart(cart);
            responseDto.setRequestStatus(RequestStatus.SUCCESS);

            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (Exception exception) {
            throw exception;
        }
    }

    @GetMapping("/getCart/{email}")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable String email) {
        try {
            TokensDto tokensDto = new TokensDto();
            tokensDto.setAccessToken(httpServletRequest.getHeaders(HttpHeaders.SET_COOKIE).toString());
            tokensDto.setRefreshToken(httpServletRequest.getHeaders(HttpHeaders.SET_COOKIE2).toString());
            Cart cart = cartService.getCart(email, tokensDto);
            CartResponseDto responseDto = fromCart(cart);
            responseDto.setRequestStatus(RequestStatus.SUCCESS);

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }

    @DeleteMapping("/removeItem")
    public ResponseEntity<CartResponseDto> removeFromCart(@RequestBody RemoveFromCartRequestDto removeFromCartRequestDto) {
        try {
            TokensDto tokensDto = new TokensDto();
            tokensDto.setAccessToken(httpServletRequest.getHeaders(HttpHeaders.SET_COOKIE).toString());
            tokensDto.setRefreshToken(httpServletRequest.getHeaders(HttpHeaders.SET_COOKIE2).toString());
            Cart cart = cartService.removeProduct(removeFromCartRequestDto.getEmail(), removeFromCartRequestDto.getProductId(),
                    removeFromCartRequestDto.getQuantity(), tokensDto);
            CartResponseDto responseDto = fromCart(cart);
            responseDto.setRequestStatus(RequestStatus.SUCCESS);

            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } catch (Exception exception) {
            throw exception;
        }
    }

    // To be Implemented
    @GetMapping("/checkout/{email}")
    public ResponseEntity<String> checkout(@PathVariable String email) {
        return new ResponseEntity<>("In Progress", HttpStatus.OK);
    }

    private CartResponseDto fromCart(Cart cart) {
        CartResponseDto cartResponseDto = new CartResponseDto();
        cartResponseDto.setCartTotal(cart.getCartTotalPrice());
        cartResponseDto.setCartItems(cart.getCartItems());
        cartResponseDto.setUuid(cart.getUuid());
        cartResponseDto.setEmail(cart.getEmail());

        return cartResponseDto;
    }
}
