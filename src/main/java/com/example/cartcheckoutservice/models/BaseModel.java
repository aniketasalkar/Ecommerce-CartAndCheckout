package com.example.cartcheckoutservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
public class BaseModel {
    @Id
    private String id;
    private Date createdAt;
    private Date updatedAt;
}
