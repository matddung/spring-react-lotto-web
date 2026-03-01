package com.studyjun.lottoweb.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ApiResponse {

    @Schema( type = "boolean", example = "true", description="요청이 성공적으로 처리되었는지 여부입니다.")
    private boolean success;

    @Schema( type = "object", example = "data", description="응답 데이터를 감싸 표현합니다. object 형식으로 표현합니다.")
    private Object data;

    public ApiResponse(){};

    @Builder
    public ApiResponse(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public static ApiResponse success(Object data) {
        return ApiResponse.builder()
                .success(true)
                .data(data)
                .build();
    }
}