package com.studyjun.lottoweb.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ApiResponseFactory {

    public ResponseEntity<ApiResponse> ok(Object data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    public ResponseEntity<ApiResponse> created(Object data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data));
    }
}