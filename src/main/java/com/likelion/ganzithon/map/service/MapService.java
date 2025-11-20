package com.likelion.ganzithon.map.service;

import com.likelion.ganzithon.exception.CustomException;
import com.likelion.ganzithon.exception.status.ErrorStatus;
import com.likelion.ganzithon.map.dto.MarkerDto;
import com.likelion.ganzithon.report.domain.Report;
import com.likelion.ganzithon.report.repository.ReportRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapService {

    private final ReportRepository reportRepository;

    public MapService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public List<MarkerDto> getMarkers(List<String> filters, double lat, double lng, double radiusKm) {

        List<Report> reports = reportRepository.findAll();

        List<MarkerDto> result = reports.stream()
                // 필터 체크
                .filter(report -> filters.contains(report.getSourceType().name().toLowerCase()))
                // 반경 체크
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
                .collect(Collectors.toList());

        // *** 여기서 MAP404 던지기 ***
        if (result.isEmpty()) {
            throw new CustomException(ErrorStatus.MAP_NOT_FOUND);
        }

        return result;
    }

    // 거리 계산 - radius
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
