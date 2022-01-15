package com.cemonan.microservices.serviceregistry.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@RestController
public class ExceptionHandler
extends ResponseEntityExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler
    public final ResponseEntity<Object> handleServiceNotFoundException(ServiceNotFoundException ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(
          new Date(),
          ex.getMessage(),
          request.getDescription(false)
        );

        return new ResponseEntity(response, HttpStatus.NOT_FOUND);
    }
}
