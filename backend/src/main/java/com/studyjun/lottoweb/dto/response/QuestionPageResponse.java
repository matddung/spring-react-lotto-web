package com.studyjun.lottoweb.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class QuestionPageResponse {
    private List<QuestionSummaryResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}