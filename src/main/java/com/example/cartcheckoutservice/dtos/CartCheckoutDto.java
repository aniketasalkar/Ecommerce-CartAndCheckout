package com.example.cartcheckoutservice.dtos;

import com.example.cartcheckoutservice.validators.AllowedPaymentMethods;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartCheckoutDto {
    @NotNull(message = "Payment method cannot be null")
    @AllowedPaymentMethods(
            value = {PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD, PaymentMethod.NET_BANKING , PaymentMethod.UPI, PaymentMethod.CASH}, // Allowed values
            message = "Invalid payment method. Allowed: CREDIT_CARD, DEBIT_CARD, NET_BANKING, UPI, CASH"
    )
    private PaymentMethod paymentMethod;
}
