package com.studyjun.lottoweb.exception;

import com.studyjun.lottoweb.dto.response.ApiResponse;
import com.studyjun.lottoweb.dto.response.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(StackOverflowError.class)
    public ResponseEntity<String> handleStackOverflowError(StackOverflowError ex) {
        log.error("Stack overflow error occurred", ex);
        return new ResponseEntity<>("Stack overflow error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("An error occurred: ", ex);
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DefaultException.class)
    public ResponseEntity<ApiResponse> handleDefaultException(DefaultException ex) {
        ApiResponse apiResponse = ApiResponse.builder()
                .check(false)
                .information(Message.builder().message(ex.getMessage()).build())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}