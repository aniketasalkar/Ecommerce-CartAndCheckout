package com.example.cartcheckoutservice.dtos;

import com.example.cartcheckoutservice.models.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private RequestStatus requestStatus;
}
