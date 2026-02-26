package com.studyjun.lottoweb.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ApiResponse {

    @Schema( type = "boolean", example = "true", description="올바르게 로직을 처리했으면 True, 아니면 False를 반환합니다.")
    private boolean check;
    
    @Schema( type = "object", example = "data", description="응답 데이터를 감싸 표현합니다. object 형식으로 표현합니다.")
    private Object data;
    
    public ApiResponse(){};

    @Builder
    public ApiResponse(boolean check, Object data) {
        this.check = check;
        this.data = data;
    }

    public static ApiResponse success(Object data) {
        return ApiResponse.builder()
                .check(true)
                .data(data)
                .build();
    }
}
