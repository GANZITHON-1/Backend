package com.likelion.ganzithon.mypage.dto;

import com.likelion.ganzithon.report.domain.Report;

public record MyReportDto(
        Long reportId,
        String title,
        String location,
        Double locationLat,
        Double LocationLng
) {
    public static MyReportDto from(Report report) {
        // roadAddress가 있으면 roadAddress 사용, 없으면 lotAddress 사용
        String location = report.getRoadAddress() != null && !report.getRoadAddress().isBlank()
                ? report.getRoadAddress()
                : report.getLotAddress();

        return new MyReportDto(
                report.getId(),
                report.getTitle(),
                location,
                report.getLatitude(),
                report.getLongitude()
        );
    }
}

