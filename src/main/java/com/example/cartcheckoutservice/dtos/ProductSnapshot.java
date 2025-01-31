package com.example.cartcheckoutservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductSnapshot {
//    @JsonProperty("productName")
    private String productName;

//    @JsonProperty("price")
    private double price;

//    @JsonProperty("description")
    private String description;

//    @JsonProperty("imageUrl")
    private String imageUrl;
}
