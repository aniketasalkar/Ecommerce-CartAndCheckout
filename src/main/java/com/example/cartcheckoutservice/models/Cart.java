package com.example.cartcheckoutservice.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@Document(collection = "carts")
public class Cart extends BaseModel implements Serializable {
    private String email;
    private List<CartItem> cartItems;
    private Double cartTotalPrice;
}
