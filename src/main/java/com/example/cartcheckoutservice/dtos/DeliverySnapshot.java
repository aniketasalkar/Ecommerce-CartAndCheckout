package com.example.cartcheckoutservice.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeliverySnapshot {
//    @JsonProperty("name")
    private String name;

//    @JsonProperty("phone")
    private String phone;

//    @JsonProperty("street")
    private String street;

//    @JsonProperty("city")
    private String city;

//    @JsonProperty("state")
    private String state;

//    @JsonProperty("zip")
    private String zip;
}
