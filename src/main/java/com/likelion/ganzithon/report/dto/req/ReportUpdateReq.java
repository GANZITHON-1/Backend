package com.likelion.ganzithon.report.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "신고 수정 요청 DTO")
public record ReportUpdateReq(

        @Schema(description = "수정할 제목", example = "제목 수정 예시")
        String title,

        @Schema(description = "수정할 설명", example = "수정된 신고 상세 내용")
        String description,

        @Schema(description = "수정할 이미지 URL", example = "https://example.com/new.jpg")
        String imageUrl
) {}
