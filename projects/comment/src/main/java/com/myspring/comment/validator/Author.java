package com.myspring.comment.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AuthorValidator.class)
@Documented
public @interface Author {
    String message() default "작성자명이 잘못되었습니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
