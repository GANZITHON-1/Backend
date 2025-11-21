package com.likelion.ganzithon.publicdata.cctv2.service;

import com.likelion.ganzithon.publicdata.cctv2.dto.Cctv2ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class Cctv2ApiCaller {

    private final RestTemplate restTemplate;

    @Value("${public.api.its-cctv-url}")
    private String apiUrl;

    @Value("${public.api.its-cctv-key}")
    private String apiKey;

    private static final String ROAD_TYPE = "its"; // 도로 유형 (ex: 고속도로)
    private static final String CCTV_TYPE = "4";  // CCTV 유형 (4: 실시간 스트리밍 HTTPS 권장)
    private static final String GET_TYPE = "json"; // 출력 형식

    public List<Cctv2ApiResponse.CctvItem> fetchCctvFeatures(double centerLat, double centerLng, double radiusKm) {

        // 1. 원형 반경을 사각형 경계 박스로 변환
        BoxCoordinates box = calculateBoundingBox(centerLat, centerLng, radiusKm);

        // 2. API 호출 URL 생성 (ITS 형식)
        URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("apiKey", apiKey)
                .queryParam("type", ROAD_TYPE)
                .queryParam("cctvType", CCTV_TYPE)
                // 경계 박스
                .queryParam("minX", String.format("%.6f", box.minLng()))
                .queryParam("maxX", String.format("%.6f", box.maxLng()))
                .queryParam("minY", String.format("%.6f", box.minLat()))
                .queryParam("maxY", String.format("%.6f", box.maxLat()))
                .queryParam("getType", GET_TYPE)
                .build()
                .toUri();

        log.info("Fetching CCTV data from ITS: {}", uri);

        try {
            Cctv2ApiResponse response = restTemplate.getForObject(uri, Cctv2ApiResponse.class);

            if (response == null || response.response() == null) {
                log.warn("No response received from ITS API.");
                return List.of();
            }

            if (response.response().data() == null) {
                return List.of();
            }

            return response.response().data();

        } catch (Exception e) {
            log.error("Failed to fetch CCTV data from ITS API.", e);
            return List.of();
        }
    }

    private record BoxCoordinates(double minLat, double minLng, double maxLat, double maxLng) {}

    private BoxCoordinates calculateBoundingBox(double centerLat, double centerLng, double radiusKm) {
        final double LAT_DEGREE_PER_KM = 0.009009;
        final double AVG_LNG_DEGREE_PER_KM = 0.0112;

        double latDelta = radiusKm * LAT_DEGREE_PER_KM;
        double lngDelta = radiusKm * AVG_LNG_DEGREE_PER_KM / Math.cos(Math.toRadians(centerLat));

        return new BoxCoordinates(
                centerLat - latDelta,
                centerLng - lngDelta,
                centerLat + latDelta,
                centerLng + lngDelta
        );
    }
}