package com.example.cartcheckoutservice.validators;

import com.example.cartcheckoutservice.dtos.PaymentMethod;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedPaymentMethodsValidator.class)
public @interface AllowedPaymentMethods {
    String message() default "Invalid payment method";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    PaymentMethod[] value(); // Allowed enum values
}
