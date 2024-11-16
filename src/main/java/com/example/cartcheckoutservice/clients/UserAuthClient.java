package com.example.cartcheckoutservice.clients;

import com.example.cartcheckoutservice.dtos.TokensDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "AUTHENTICATIONSERVICE")
public interface UserAuthClient {
    @PostMapping("/api/auth//{email}/validateToken")
    Boolean validateToken(@PathVariable("email") String email, TokensDto tokensDtoss);
}
