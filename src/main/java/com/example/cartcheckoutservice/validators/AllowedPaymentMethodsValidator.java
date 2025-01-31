package com.example.cartcheckoutservice.validators;

import com.example.cartcheckoutservice.dtos.PaymentMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class AllowedPaymentMethodsValidator
        implements ConstraintValidator<AllowedPaymentMethods, PaymentMethod> {

    private List<PaymentMethod> allowedValues;

    @Override
    public void initialize(AllowedPaymentMethods constraintAnnotation) {
        allowedValues = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(PaymentMethod value, ConstraintValidatorContext context) {
        // Allow null (combine with @NotNull if needed)
        if (value == null) return true;
        return allowedValues.contains(value);
    }
}
