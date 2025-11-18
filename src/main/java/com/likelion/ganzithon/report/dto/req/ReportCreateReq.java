package com.likelion.ganzithon.report.dto.req;

import com.likelion.ganzithon.report.domain.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportCreateReq(
        @NotNull(message = "userId는 필수 값입니다.")
        Long userId,

        @NotBlank(message = "title은 필수 값입니다.")
        String title,

        @NotBlank(message = "description은 필수 값입니다.")
        String description,

        @NotBlank(message = "imageUrl은 필수 값입니다.")
        String imageUrl,

        String roadAddress,
        String lotAddress,

        @NotNull(message = "latitude는 필수 값입니다.")
        Double latitude,

        @NotNull(message = "longitude는 필수 값입니다.")
        Double longitude,

        @NotNull(message = "sourceType은 필수 값입니다.")
        SourceType sourceType
) {}
