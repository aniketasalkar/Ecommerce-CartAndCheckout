package com.example.cartcheckoutservice.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
public class BaseModel implements Serializable {
    @Id
    private String id;
    private Date createdAt;
    private Date updatedAt;
    private String uuid;
}
