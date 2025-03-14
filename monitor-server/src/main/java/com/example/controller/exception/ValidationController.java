package com.example.controller.exception;


import com.example.entity.RestBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.swing.plaf.PanelUI;
@Slf4j
@RestControllerAdvice
public class ValidationController {
    @ExceptionHandler(ValidationException.class)
    public RestBean<Void> validationException(ValidationException exception) {
        log.warn("Resolve:[{}:{}]",exception.getClass().getName(), exception.getMessage());
        return RestBean.failure(400,"请求参数有误");
    }
}
