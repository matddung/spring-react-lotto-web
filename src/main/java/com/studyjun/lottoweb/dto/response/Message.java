package com.studyjun.lottoweb.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Data
public class Message {

    @Schema( type = "string", example = "메시지 문구를 출력합니다.", description="메시지 입니다.")
    private String message;

    @Builder
    public Message(String message) {
        this.message = message;
    }
}
