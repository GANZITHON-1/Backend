package com.likelion.ganzithon.report.dto.req;

public record ReportUpdateReq(
        String title,
        String description,
        String imageUrl
) {
}
