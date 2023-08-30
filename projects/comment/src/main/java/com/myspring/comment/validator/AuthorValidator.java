package com.myspring.comment.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.thymeleaf.util.StringUtils;

public class AuthorValidator implements ConstraintValidator<Author, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.equals("관리자", s)) {
            return false;
        }
        return true;
    }
}
