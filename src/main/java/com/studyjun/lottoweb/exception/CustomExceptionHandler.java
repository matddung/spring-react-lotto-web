package com.studyjun.lottoweb.exception;

import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(DefaultException.class)
    public ResponseEntity<ApiResponse> handleDefaultException(DefaultException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .check(false)
                .information(Message.builder().message(ex.getMessage()).build())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}