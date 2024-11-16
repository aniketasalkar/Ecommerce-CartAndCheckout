package com.example.cartcheckoutservice.repositories;

import com.example.cartcheckoutservice.models.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    Cart save(Cart cart);
    Optional<Cart> findByEmail(String email);
}
