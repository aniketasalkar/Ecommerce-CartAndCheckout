package com.example.cartcheckoutservice.clients;

import com.example.cartcheckoutservice.dtos.AddressDto;
import com.example.cartcheckoutservice.dtos.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("USERMANAGEMENTSERVICE")
public interface UserManagementClient {
    @GetMapping("/api/users/{email}/userDetails")
    ResponseEntity<UserResponseDto> getUser(@PathVariable String email);

    @GetMapping("/api/address/default_address/{userId}")
    ResponseEntity<AddressDto> getDefaultAddress(@PathVariable Long userId);
}
