package com.likelion.ganzithon.report.dto.res;

import com.likelion.ganzithon.report.domain.Report;
import com.likelion.ganzithon.report.domain.ReportStatus;
import com.likelion.ganzithon.report.domain.SourceType;

public record ReportRes(
        Long reportId,
        Long userId,

        String title,
        String description,

        String imageUrl,
        String roadAddress,
        String lotAddress,

        Double latitude,
        Double longitude,

        SourceType sourceType,
        ReportStatus status,

        String createdAt,
        String updatedAt
) {

    public static ReportRes from(Report r) {
        return new ReportRes(
                r.getId(),
                r.getUserId(),
                r.getTitle(),
                r.getDescription(),
                r.getImageUrl(),
                r.getRoadAddress(),
                r.getLotAddress(),
                r.getLatitude(),
                r.getLongitude(),
                r.getSourceType(),
                r.getStatus(),
                r.getCreatedAt().toString(),
                r.getUpdatedAt().toString()
        );
    }
}
