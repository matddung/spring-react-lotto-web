package com.studyjun.lottoweb.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponse {
    private final boolean success;
    private final String code;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;

    @Builder
    public ErrorResponse(boolean success, String code, String message, String path, LocalDateTime timestamp) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }
}