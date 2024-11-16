package com.example.cartcheckoutservice.clients;

import com.example.cartcheckoutservice.dtos.ProductCatalogClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCTCATALOGSERVICE")
public interface ProductCatalogClient {
    @GetMapping("/api/productCategoryService/products/{id}")
    ProductCatalogClientResponseDto getProduct(@PathVariable Long id);
}
