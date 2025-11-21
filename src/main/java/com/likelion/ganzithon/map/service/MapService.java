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

import com.likelion.ganzithon.publicdata.cctv2.dto.Cctv2ApiResponse;
import com.likelion.ganzithon.publicdata.cctv2.service.Cctv2ApiCaller;

@Slf4j
@Service
public class MapService {

    private static final long CCTV_ID_OFFSET = 1_000_000L;
    private static final long CCTV2_ID_OFFSET = 4_000_000L;
    private static final long BELL_ID_OFFSET = 2_000_000L;
    private static final long TRAFFIC_ID_OFFSET = 3_000_000L;

    private final ReportRepository reportRepository;
    private final CctvApiCaller cctvApiCaller;
    private final Cctv2ApiCaller cctv2ApiCaller;
    private final EmgBellApiCaller emgBellApiCaller;
    private final TrafficApiCaller trafficApiCaller;
    private final RegionService regionService;

    private final Map<Long, CctvApiResponse.Feature> cctvCache = new HashMap<>();
    private final Map<Long, Cctv2ApiResponse.CctvItem> cctv2Cache = new HashMap<>();
    private final Map<Long, EmgBellData> bellCache = new HashMap<>();
    private final Map<Long, TrafficApiResponse.Item> trafficCache = new HashMap<>();

    public MapService(ReportRepository reportRepository,
                      CctvApiCaller cctvApiCaller,
                      Cctv2ApiCaller cctv2ApiCaller,
                      EmgBellApiCaller emgBellApiCaller,
                      TrafficApiCaller trafficApiCaller,
                      RegionService regionService) {
        this.reportRepository = reportRepository;
        this.cctvApiCaller = cctvApiCaller;
        this.cctv2ApiCaller = cctv2ApiCaller;
        this.emgBellApiCaller = emgBellApiCaller;
        this.trafficApiCaller = trafficApiCaller;
        this.regionService = regionService;
    }

    public List<MarkerDto> getMarkers(List<String> filters, double lat, double lng, double radiusKm) {

        // 캐시 초기화 (마커 조회 시 항상 새로운 데이터를 가져오기 위함)
        cctvCache.clear();
        cctv2Cache.clear();
        bellCache.clear();
        trafficCache.clear();

        List<Report> reports = reportRepository.findAll();
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

        // 2. CCTV (VWorld WFS) -filter cctv2로 변경 -api 오류로 인해 보류
        if (filters.contains("cctv2")) {
            List<CctvApiResponse.Feature> features = cctvApiCaller.fetchCctvFeatures(lat, lng, radiusKm);
            long idCounter = CCTV_ID_OFFSET;

            for (CctvApiResponse.Feature feature : features) {
                double lngValue = feature.geometry().coordinates().get(0);
                double latValue = feature.geometry().coordinates().get(1);

                // 마커 생성 및 캐시 저장
                long markerId = idCounter++;
                totalMarkers.add(new MarkerDto(
                        markerId,
                        "CCTV 설치 (VWorld)",
                        feature.properties().locate(),
                        latValue,
                        lngValue,
                        "cctv2",
                        SourceType.PUBLIC
                ));
                cctvCache.put(markerId, feature);
            }
        }

        // 2-2. CCTV2 (ITS API) -filter cctv로 변경
        if (filters.contains("cctv")) {
            List<Cctv2ApiResponse.CctvItem> items = cctv2ApiCaller.fetchCctvFeatures(lat, lng, radiusKm);
            long idCounter = CCTV2_ID_OFFSET;
            if (items != null) {
                for (Cctv2ApiResponse.CctvItem item : items) {
                    try {
                        double lngValue = Double.parseDouble(item.coordX());
                        double latValue = Double.parseDouble(item.coordY());

                        if (!isWithinRadius(lat, lng, latValue, lngValue, radiusKm)) continue;

                        // 마커 생성 및 캐시 저장
                        long markerId = idCounter++;
                        totalMarkers.add(new MarkerDto(
                                markerId,
                                item.cctvName(),
                                item.cctvName(),
                                latValue,
                                lngValue,
                                "cctv",
                                SourceType.PUBLIC
                        ));
                        cctv2Cache.put(markerId, item);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid coordinate format received from ITS API: X={}, Y={}", item.coordX(), item.coordY());
                    }
                }
            }
        }

        // 3. 안전비상벨
        if (filters.contains("bell")) {
            List<EmgBellData> bells = emgBellApiCaller.fetchEmgBells(1, 1000); // 현재 API는 전체 데이터를 가져오는 방식 가정
            long idCounter = BELL_ID_OFFSET;

            for (EmgBellData bell : bells) {
                if (!isWithinRadius(lat, lng, bell.latitude(), bell.longitude(), radiusKm)) continue;

                String address = bell.roadAddress() != null && !bell.roadAddress().isBlank() ? bell.roadAddress() : bell.lotAddress();

                // 마커 생성 및 캐시 저장
                long markerId = idCounter++;
                totalMarkers.add(new MarkerDto(
                        markerId,
                        "안전비상벨",
                        address,
                        bell.latitude(),
                        bell.longitude(),
                        "bell",
                        SourceType.PUBLIC
                ));
                bellCache.put(markerId, bell); // 🚨 캐시 저장
            }
        }

        // 4. Traffic
        if (filters.contains("traffic")) {
            RegionCodeDto regionCodes = regionService.getRegionCodesByCoordinates(lat, lng);
            List<TrafficApiResponse.Item> items = trafficApiCaller.fetchTrafficDataByRegion(regionCodes.siDo(), regionCodes.guGun());
            long idCounter = TRAFFIC_ID_OFFSET;

            for (TrafficApiResponse.Item item : items) {
                double latValue = Double.parseDouble(item.laCrd().toString());
                double lngValue = Double.parseDouble(item.loCrd().toString());
                if (!isWithinRadius(lat, lng, latValue, lngValue, radiusKm)) continue;

                String title = String.format("%s 다발지역 (사고: %d건)", item.spotNm(), item.occrrncCnt());

                // 마커 생성 및 캐시 저장
                long markerId = idCounter++;
                totalMarkers.add(new MarkerDto(
                        markerId,
                        title,
                        item.sidoSggNm() + " " + item.spotNm(),
                        latValue,
                        lngValue,
                        "traffic",
                        SourceType.PUBLIC
                ));
                trafficCache.put(markerId, item);
            }
        }

        if (totalMarkers.isEmpty()) {
            throw new CustomException(ErrorStatus.MAP_NOT_FOUND);
        }

        return totalMarkers;
    }


    public PublicMarkerDetailDto getPublicMarkerDetail(Long id) {

        // 1. CCTV (VWorld WFS): 1,000,000 ~ 1,999,999
        if (id >= CCTV_ID_OFFSET && id < BELL_ID_OFFSET) {
            CctvApiResponse.Feature feature = cctvCache.get(id);
            if (feature == null) throw new CustomException(ErrorStatus.MAP_NOT_FOUND);

            return new PublicMarkerDetailDto(
                    id,
                    "PUBLIC",
                    "CCTV 설치 (VWorld)",
                    "cctv2",
                    feature.properties().locate(),
                    null,
                    feature.geometry().coordinates().get(1),
                    feature.geometry().coordinates().get(0),
                    null
            );
        }

        // 2. 안전비상벨 (BELL): 2,000,000 ~ 2,999,999
        if (id >= BELL_ID_OFFSET && id < TRAFFIC_ID_OFFSET) {
            EmgBellData bell = bellCache.get(id); // 🚨 캐시에서 조회
            if (bell == null) throw new CustomException(ErrorStatus.MAP_NOT_FOUND);

            String address = bell.roadAddress() != null && !bell.roadAddress().isBlank() ? bell.roadAddress() : bell.lotAddress();

            return new PublicMarkerDetailDto(
                    id,
                    "PUBLIC",
                    "안전비상벨",
                    "bell",
                    address,
                    null,
                    bell.latitude(),
                    bell.longitude(),
                    null
            );
        }

        // 3. Traffic: 3,000,000 ~ 3,999,999
        if (id >= TRAFFIC_ID_OFFSET && id < CCTV2_ID_OFFSET) {
            TrafficApiResponse.Item item = trafficCache.get(id);
            if (item == null) throw new CustomException(ErrorStatus.MAP_NOT_FOUND);

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

        // 4. CCTV2 (ITS API): 4,000,000 ~
        if (id >= CCTV2_ID_OFFSET) {
            Cctv2ApiResponse.CctvItem item = cctv2Cache.get(id);
            if (item == null) throw new CustomException(ErrorStatus.MAP_NOT_FOUND);

            double latValue = Double.parseDouble(item.coordY());
            double lngValue = Double.parseDouble(item.coordX());

            return new PublicMarkerDetailDto(
                    id,
                    "PUBLIC",
                    item.cctvName(),
                    "cctv",
                    item.cctvName(),
                    null,
                    latValue,
                    lngValue,
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