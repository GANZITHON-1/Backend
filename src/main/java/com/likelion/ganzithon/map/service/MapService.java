package com.likelion.ganzithon.map.service;

import com.likelion.ganzithon.exception.CustomException;
import com.likelion.ganzithon.exception.status.ErrorStatus;
import com.likelion.ganzithon.map.dto.MarkerDto;
import com.likelion.ganzithon.publicdata.cctv.dto.CctvApiResponse;
import com.likelion.ganzithon.publicdata.cctv.service.CctvApiCaller;
import com.likelion.ganzithon.report.domain.Report;
import com.likelion.ganzithon.report.domain.SourceType;
import com.likelion.ganzithon.report.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MapService {

    private final ReportRepository reportRepository;
    private final CctvApiCaller cctvApiCaller;

    public MapService(ReportRepository reportRepository, CctvApiCaller cctvApiCaller) {
        this.reportRepository = reportRepository;
        this.cctvApiCaller = cctvApiCaller;
    }

    public List<MarkerDto> getMarkers(List<String> filters, double lat, double lng, double radiusKm) {

        List<MarkerDto> totalMarkers = new ArrayList<>();

        // 1. 사용자 제보
        List<Report> reports = reportRepository.findAll();
        List<MarkerDto> reportMarkers = reports.stream()
                .filter(report -> filters.contains(report.getSourceType().name().toLowerCase()))
                .filter(report -> isWithinRadius(lat, lng, report.getLatitude(), report.getLongitude(), radiusKm))
                .map(report -> new MarkerDto(
                        report.getId(),
                        report.getTitle(),
                        report.getLotAddress(),
                        report.getLatitude(),
                        report.getLongitude(),
                        report.getSourceType().name().toLowerCase(),
                        report.getSourceType()
                ))
                .toList();

        totalMarkers.addAll(reportMarkers);

        // 2. 공공 데이터(CCTV)
        if (filters.contains("cctv")) {

            Long maxReportId = reports.stream()
                    .map(Report::getId)
                    .max(Comparator.naturalOrder())
                    .orElse(0L);

            // CCTV ID의 시작점 설정
            long cctvIdCounter = maxReportId + 1L;

            List<CctvApiResponse.Feature> features = cctvApiCaller.fetchCctvFeatures(lat, lng, radiusKm);
            List<MarkerDto> cctvMarkers = new ArrayList<>();

            for (CctvApiResponse.Feature feature : features) {
                double lngValue = feature.geometry().coordinates().get(0);
                double latValue = feature.geometry().coordinates().get(1);

                MarkerDto marker = new MarkerDto(
                        cctvIdCounter++,
                        "CCTV 설치",
                        feature.properties().locate(),
                        latValue,
                        lngValue,
                        "cctv",
                        SourceType.PUBLIC
                );
                cctvMarkers.add(marker);
            }

            totalMarkers.addAll(cctvMarkers);
        }

        // 3. 결과 반환
        if (totalMarkers.isEmpty()) {
            throw new CustomException(ErrorStatus.MAP_NOT_FOUND);
        }

        return totalMarkers;
    }

    // 거리 계산
    private boolean isWithinRadius(double centerLat, double centerLng,
                                   double targetLat, double targetLng, double radiusKm) {
        final int EARTH_RADIUS = 6371;
        double dLat = Math.toRadians(targetLat - centerLat);
        double dLng = Math.toRadians(targetLng - centerLng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(centerLat)) * Math.cos(Math.toRadians(targetLat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return distance <= radiusKm;
    }
}