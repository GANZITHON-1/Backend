package com.likelion.ganzithon.map.service;

import com.likelion.ganzithon.exception.CustomException;
import com.likelion.ganzithon.exception.status.ErrorStatus;
import com.likelion.ganzithon.map.dto.MarkerDto;
import com.likelion.ganzithon.map.dto.PublicMarkerDetailDto;
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

import java.util.*;

@Slf4j
@Service
public class MapService {

    private static final long CCTV_ID_OFFSET = 1_000_000L;
    private static final long BELL_ID_OFFSET = 2_000_000L;
    private static final long TRAFFIC_ID_OFFSET = 3_000_000L;

    private final ReportRepository reportRepository;
    private final CctvApiCaller cctvApiCaller;
    private final EmgBellApiCaller emgBellApiCaller;
    private final TrafficApiCaller trafficApiCaller;
    private final RegionService regionService;

    private final Map<Long, CctvApiResponse.Feature> cctvCache = new HashMap<>();

    public MapService(ReportRepository reportRepository,
                      CctvApiCaller cctvApiCaller,
                      EmgBellApiCaller emgBellApiCaller,
                      TrafficApiCaller trafficApiCaller,
                      RegionService regionService) {
        this.reportRepository = reportRepository;
        this.cctvApiCaller = cctvApiCaller;
        this.emgBellApiCaller = emgBellApiCaller;
        this.trafficApiCaller = trafficApiCaller;
        this.regionService = regionService;
    }

    public List<MarkerDto> getMarkers(List<String> filters, double lat, double lng, double radiusKm) {
        List<Report> reports = reportRepository.findAll();

        // 1. 사용자 제보
        List<MarkerDto> totalMarkers = new ArrayList<>(reports.stream()
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
                .toList());

        // 2. CCTV
        if (filters.contains("cctv")) {
            List<CctvApiResponse.Feature> features = cctvApiCaller.fetchCctvFeatures(lat, lng, radiusKm);
            long cctvIdCounter = CCTV_ID_OFFSET;

            for (CctvApiResponse.Feature feature : features) {
                double lngValue = feature.geometry().coordinates().get(0);
                double latValue = feature.geometry().coordinates().get(1);

                MarkerDto marker = new MarkerDto(
                        cctvIdCounter,
                        "CCTV 설치",
                        feature.properties().locate(),
                        latValue,
                        lngValue,
                        "cctv",
                        SourceType.PUBLIC
                );

                totalMarkers.add(marker);
                cctvCache.put(cctvIdCounter, feature); // 캐시에 저장
                cctvIdCounter++;
            }
        }

        // 3. 안전비상벨
        if (filters.contains("bell")) {
            long bellIdCounter = BELL_ID_OFFSET;
            List<EmgBellData> bells = emgBellApiCaller.fetchEmgBells(1, 1000);

            for (EmgBellData bell : bells) {
                if (!isWithinRadius(lat, lng, bell.latitude(), bell.longitude(), radiusKm)) continue;

                String address = bell.roadAddress() != null && !bell.roadAddress().isBlank() ? bell.roadAddress() : bell.lotAddress();
                MarkerDto marker = new MarkerDto(
                        bellIdCounter++,
                        "안전비상벨",
                        address,
                        bell.latitude(),
                        bell.longitude(),
                        "bell",
                        SourceType.PUBLIC
                );
                totalMarkers.add(marker);
            }
        }

        // 4. Traffic
        if (filters.contains("traffic")) {
            long trafficIdCounter = TRAFFIC_ID_OFFSET;
            RegionCodeDto regionCodes = regionService.getRegionCodesByCoordinates(lat, lng);
            List<TrafficApiResponse.Item> items = trafficApiCaller.fetchTrafficDataByRegion(regionCodes.siDo(), regionCodes.guGun());

            for (TrafficApiResponse.Item item : items) {
                double latValue = Double.parseDouble(item.laCrd().toString());
                double lngValue = Double.parseDouble(item.loCrd().toString());
                if (!isWithinRadius(lat, lng, latValue, lngValue, radiusKm)) continue;

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
                totalMarkers.add(marker);
            }
        }

        if (totalMarkers.isEmpty()) {
            throw new CustomException(ErrorStatus.MAP_NOT_FOUND);
        }

        return totalMarkers;
    }

    public PublicMarkerDetailDto getPublicMarkerDetail(Long id) {
        // CCTV
        if (id >= CCTV_ID_OFFSET && id < BELL_ID_OFFSET) {
            CctvApiResponse.Feature feature = cctvCache.get(id);
            if (feature == null) throw new CustomException(ErrorStatus.MAP_NOT_FOUND);

            return new PublicMarkerDetailDto(
                    id,
                    "PUBLIC",
                    "CCTV 설치",
                    "cctv",
                    feature.properties().locate(),
                    null,
                    feature.geometry().coordinates().get(1),
                    feature.geometry().coordinates().get(0),
                    null
            );
        }

        // 안전비상벨
        if (id >= BELL_ID_OFFSET && id < TRAFFIC_ID_OFFSET) {
            List<EmgBellData> bells = emgBellApiCaller.fetchEmgBells(1, 1000);
            EmgBellData bell = bells.get((int)(id - BELL_ID_OFFSET));
            return new PublicMarkerDetailDto(
                    id,
                    "PUBLIC",
                    "안전비상벨",
                    "bell",
                    bell.roadAddress() != null && !bell.roadAddress().isBlank() ? bell.roadAddress() : bell.lotAddress(),
                    null,
                    bell.latitude(),
                    bell.longitude(),
                    null
            );
        }

        // Traffic
        if (id >= TRAFFIC_ID_OFFSET) {
            RegionCodeDto regionCodes = regionService.getRegionCodesByCoordinates(0,0);
            List<TrafficApiResponse.Item> items = trafficApiCaller.fetchTrafficDataByRegion(regionCodes.siDo(), regionCodes.guGun());
            TrafficApiResponse.Item item = items.get((int)(id - TRAFFIC_ID_OFFSET));
            return new PublicMarkerDetailDto(
                    id,
                    "PUBLIC",
                    String.format("%s 다발지역 (사고: %d건)", item.spotNm(), item.occrrncCnt()),
                    "traffic",
                    item.sidoSggNm() + " " + item.spotNm(),
                    null,
                    Double.parseDouble(item.laCrd().toString()),
                    Double.parseDouble(item.loCrd().toString()),
                    null
            );
        }

        throw new CustomException(ErrorStatus.MAP_NOT_FOUND);
    }

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
