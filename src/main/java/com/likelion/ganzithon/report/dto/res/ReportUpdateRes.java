package com.likelion.ganzithon.report.dto.res;

import com.likelion.ganzithon.report.domain.Report;

public record ReportUpdateRes(
        Long reportId,
        String title,
        String description,
        String imageUrl,
        String updatedAt
) {
    public static ReportUpdateRes from(Report r) {
        return new ReportUpdateRes(
                r.getId(),
                r.getTitle(),
                r.getDescription(),
                r.getImageUrl(),
                r.getUpdatedAt().toString()
        );
    }
}
