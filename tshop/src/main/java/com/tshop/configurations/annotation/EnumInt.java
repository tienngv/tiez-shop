package com.tshop.configurations.annotation;


import com.tshop.configurations.validator.EnumIntValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumIntValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumInt {
    String message() default "Giá trị không hợp lệ"; // có thể override khi dùng
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int[] values(); // danh sách giá trị hợp lệ
}

