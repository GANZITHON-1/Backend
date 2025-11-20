package com.likelion.ganzithon.map.service;

import com.likelion.ganzithon.exception.CustomException;
import com.likelion.ganzithon.exception.status.ErrorStatus;
import com.likelion.ganzithon.map.dto.MarkerDto;
import com.likelion.ganzithon.map.dto.RegionCodeDto;
import com.likelion.ganzithon.publicdata.cctv.dto.CctvApiResponse;
import com.likelion.ganzithon.publicdata.cctv.service.CctvApiCaller;
import com.likelion.ganzithon.publicdata.emgbell.dto.EmgBellData;
import com.likelion.ganzithon.publicdata.emgbell.service.EmgBellApiCaller;
import com.likelion.ganzithon.publicdata.traffic.dto.TrafficApiResponse;
import com.likelion.ganzithon.publicdata.traffic.service.TrafficApiCaller;
import com.likelion.ganzithon.report.domain.Report;
import com.likelion.ganzithon.report.domain.SourceType;
import com.likelion.ganzithon.report.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class MapService {

    private final ReportRepository reportRepository;
    private final CctvApiCaller cctvApiCaller;
    private final EmgBellApiCaller emgBellApiCaller;
    private final TrafficApiCaller trafficApiCaller;
    private final RegionService regionService;

    public MapService(ReportRepository reportRepository,
                      CctvApiCaller cctvApiCaller,
                      EmgBellApiCaller emgBellApiCaller,
                      TrafficApiCaller trafficApiCaller, RegionService regionService
    ) {
        this.reportRepository = reportRepository;
        this.cctvApiCaller = cctvApiCaller;
        this.emgBellApiCaller = emgBellApiCaller;
        this.trafficApiCaller = trafficApiCaller;
        this.regionService = regionService;
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

        // 2.2 공공 데이터(안전비상벨)
        if (filters.contains("bell")) {

            Long maxReportIdForBell = reports.stream()
                    .map(Report::getId)
                    .max(Comparator.naturalOrder())
                    .orElse(0L);

            long bellIdCounter = maxReportIdForBell + 1_000_000L;

            List<EmgBellData> bells = emgBellApiCaller.fetchEmgBells(1, 1000);
            List<MarkerDto> bellMarkers = new ArrayList<>();

            for (EmgBellData bell : bells) {
                if (!isWithinRadius(lat, lng, bell.latitude(), bell.longitude(), radiusKm)) {
                    continue;
                }

                String address = bell.roadAddress() != null && !bell.roadAddress().isBlank()
                        ? bell.roadAddress()
                        : bell.lotAddress();

                MarkerDto marker = new MarkerDto(
                        bellIdCounter++,
                        "안전비상벨",
                        address,
                        bell.latitude(),
                        bell.longitude(),
                        "bell",
                        SourceType.PUBLIC
                );
                bellMarkers.add(marker);
            }

            totalMarkers.addAll(bellMarkers);
        }

        // 2.3 공공 데이터 (Traffic)
        if (filters.contains("traffic")) {

            // Traffic 마커 ID는 무조건 1부터 시작하도록 초기화
            long trafficIdCounter = 2_000_000L;

            // 동적 지역 코드 조회
            RegionCodeDto regionCodes = regionService.getRegionCodesByCoordinates(lat, lng);
            String siDo = regionCodes.siDo();
            String guGun = regionCodes.guGun();

            List<TrafficApiResponse.Item> items = trafficApiCaller.fetchTrafficDataByRegion(siDo, guGun);
            List<MarkerDto> trafficMarkers = new ArrayList<>();

            for (TrafficApiResponse.Item item : items) {
                double latValue = Double.parseDouble(item.laCrd().toString());
                double lngValue = Double.parseDouble(item.loCrd().toString());

                if (!isWithinRadius(lat, lng, latValue, lngValue, radiusKm)) {
                    continue;
                }

                String title = String.format("%s 다발지역 (사고: %d건)", item.spotNm(), item.occrrncCnt());

                MarkerDto marker = new MarkerDto(
                        trafficIdCounter++,
                        title,
                        item.sidoSggNm() + " " + item.spotNm(),
                        latValue,
                        lngValue,
                        "traffic",
                        SourceType.PUBLIC
                );

                trafficMarkers.add(marker);
            }

            totalMarkers.addAll(trafficMarkers);
        }



        // 4. 결과 반환
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