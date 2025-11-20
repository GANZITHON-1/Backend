package com.likelion.ganzithon.map.service;

import com.likelion.ganzithon.exception.CustomException;
import com.likelion.ganzithon.exception.status.ErrorStatus;
import com.likelion.ganzithon.map.dto.MarkerDto;
import com.likelion.ganzithon.report.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapService {

    private final ReportRepository reportRepository;
    private final CctvService cctvService;
//    private final PoliceService policeService;
//    private final TrafficService trafficService;
//    private final BellService bellService;

    public MapService(ReportRepository reportRepository,
                      CctvService cctvService
//                      PoliceService policeService,
//                      TrafficService trafficService,
//                      BellService bellService
    ) {
        this.reportRepository = reportRepository;
        this.cctvService = cctvService;
//        this.policeService = policeService;
//        this.trafficService = trafficService;
//        this.bellService = bellService;
    }

    public List<MarkerDto> getMarkers(List<String> filters, double lat, double lng, double radiusKm) {

        // 사용자 제보
        List<MarkerDto> result = reportRepository.findAll().stream()
                .filter(r -> filters.contains(r.getSourceType().name().toLowerCase()))
                .filter(r -> isWithinRadius(lat, lng, r.getLatitude(), r.getLongitude(), radiusKm))
                .map(r -> new MarkerDto(
                        r.getId(),
                        r.getTitle(),
                        r.getLotAddress(),
                        r.getLatitude(),
                        r.getLongitude(),
                        r.getSourceType().name().toLowerCase(),
                        r.getSourceType())).collect(Collectors.toList());

        // cctv
        if (filters.contains("cctv")) {
            result.addAll(
                    cctvService.getCctvMarkers(lat, lng, radiusKm).findValues("features").stream()
                            .map(jsonNode -> new MarkerDto(
                                    jsonNode.get("id").asLong(),
                                    jsonNode.get("properties").get("title").asText(),
                                    jsonNode.get("properties").get("address").asText(),
                                    jsonNode.get("geometry").get("coordinates").get(1).asDouble(),
                                    jsonNode.get("geometry").get("coordinates").get(0).asDouble(),
                                    "cctv",
                                    null // SourceType
                            ))
                            .toList()
            );
        }

        // 반경 필터
        result = result.stream()
                .filter(m -> isWithinRadius(lat, lng, m.lat(), m.lng(), radiusKm))
                .toList();

        if (result.isEmpty()) throw new CustomException(ErrorStatus.MAP_NOT_FOUND);

        return result;
    }

    private boolean isWithinRadius(double centerLat, double centerLng, double targetLat, double targetLng, double radiusKm) {
        final int EARTH_RADIUS = 6371;
        double dLat = Math.toRadians(targetLat - centerLat);
        double dLng = Math.toRadians(targetLng - centerLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(centerLat)) * Math.cos(Math.toRadians(targetLat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        return distance <= radiusKm;
    }

    // 디버깅
    public void printCctvMarkers(double lat, double lng, double radiusKm) {
        List<MarkerDto> markers = cctvService.getCctvMarkers(lat, lng, radiusKm).findValues("features").stream()
                .map(jsonNode -> new MarkerDto(
                        jsonNode.get("id").asLong(),
                        jsonNode.get("properties").get("cctvname").asText(),
                        jsonNode.get("properties").get("locate").asText(),
                        jsonNode.get("geometry").get("coordinates").get(1).asDouble(),
                        jsonNode.get("geometry").get("coordinates").get(0).asDouble(),
                        "cctv",
                        null
                ))
                .toList();

        if (markers.isEmpty()) {
            System.out.println("CCTV 마커가 없습니다.");
            return;
        }

        System.out.println("총 CCTV 마커 수: " + markers.size());
        for (MarkerDto m : markers) {
            System.out.println("ID: " + m.markerId() + ", Title: " + m.title() +
                    ", Address: " + m.location() +
                    ", Lat: " + m.lat() + ", Lng: " + m.lng());
        }
    }
}