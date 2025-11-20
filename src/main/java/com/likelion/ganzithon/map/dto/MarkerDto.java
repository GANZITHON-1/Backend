package com.likelion.ganzithon.map.dto;

import com.likelion.ganzithon.report.domain.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;

public record MarkerDto(
        @Schema(description = "마커 ID", example = "1")
        Long markerId,

        @Schema(description = "신고 제목", example = "불법 주정차 차량 신고")
        String title,

        @Schema(description = "위치(도로명 또는 지번)", example = "서울시 강남구 역삼동 123-45")
        String location,

        @Schema(description = "위도", example = "37.4979")
        double lat,

        @Schema(description = "경도", example = "127.0276")
        double lng,

        @Schema(description = "필터 타입", example = "fire")
        String filterType,

        @Schema(description = "신고 유형", example = "FIRE")
        SourceType sourceType
) {}
