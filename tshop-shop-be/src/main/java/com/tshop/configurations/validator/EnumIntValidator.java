package com.tshop.configurations.validator;


import com.tshop.configurations.annotation.EnumInt;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumIntValidator implements ConstraintValidator<EnumInt, Integer> {
    private int[] acceptedValues;

    @Override
    public void initialize(EnumInt constraintAnnotation) {
        acceptedValues = constraintAnnotation.values();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return Arrays.stream(acceptedValues).anyMatch(v -> v == value);
    }
}


