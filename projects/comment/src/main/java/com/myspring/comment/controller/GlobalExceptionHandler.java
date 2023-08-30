package com.myspring.comment.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

// @RestControllerAdvice // 에러 결과를 JSON 형식의 응답 사용 시
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidError(Model model, MethodArgumentNotValidException ex) {


        String message = Optional.ofNullable(ex.getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("알 수 없는 오류가 발생했습니다.");

        model.addAttribute("message", message);
        return "error";
    }

    // Validated 오류 처리
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleInternalValidError(Model model, ConstraintViolationException ex) {

        String message = ex.getMessage();
        model.addAttribute("message", message);

        return "error";
    }
}
