package com.likelion.ganzithon.report.dto.req;

import com.likelion.ganzithon.report.domain.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "신고 생성 요청 DTO")
public record ReportCreateReq(

        @Schema(description = "신고 제목", example = "불법 주정차 차량 신고")
        @NotBlank(message = "title은 필수 값입니다.")
        String title,

        @Schema(description = "신고 상세 설명", example = "횡단보도 앞 불법 주차 차량이 있어 위험합니다.")
        @NotBlank(message = "description은 필수 값입니다.")
        String description,

        @Schema(description = "이미지 URL", example = "https://example.com/img.jpg")
        @NotBlank(message = "imageUrl은 필수 값입니다.")
        String imageUrl,

        @Schema(description = "도로명 주소", example = "서울 강남구 테헤란로 123")
        String roadAddress,

        @Schema(description = "지번 주소", example = "서울 강남구 역삼동 12-3")
        String lotAddress,

        @Schema(description = "위도", example = "37.4952")
        @NotNull(message = "latitude는 필수 값입니다.")
        Double latitude,

        @Schema(description = "경도", example = "127.0276")
        @NotNull(message = "longitude는 필수 값입니다.")
        Double longitude,

        @Schema(description = "신고 소스 타입", example = "USER")
        @NotNull(message = "sourceType은 필수 값입니다.")
        SourceType sourceType
) {}
