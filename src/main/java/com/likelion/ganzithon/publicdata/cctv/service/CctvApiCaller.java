package com.likelion.ganzithon.publicdata.cctv.service;

import com.likelion.ganzithon.publicdata.cctv.dto.CctvApiResponse;
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
public class CctvApiCaller {

    private final RestTemplate restTemplate;

    @Value("${public.api.cctv-url}")
    private String apiUrl;

    @Value("${public.api.cctv-key}")
    private String apiKey;

    @Value("${public.api.cctv-domain}")
    private String apiDomain;

    private static final String DATA_ID = "LT_P_UTISCCTV";

    public List<CctvApiResponse.Feature> fetchCctvFeatures(double centerLat, double centerLng, double radiusKm) {

        // 1. 원형 반경을 사각형 경계 박스로 변환 -api 호출 구조 상 필요
        BoxCoordinates box = calculateBoundingBox(centerLat, centerLng, radiusKm);
        String geomFilter = String.format("BOX(%.4f,%.4f,%.4f,%.4f)",
                box.minLng(), box.minLat(), box.maxLng(), box.maxLat());

        // 2. API 호출 URL 생성
        URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("service", "data")
                .queryParam("version", "2.0")
                .queryParam("request", "GetFeature")
                .queryParam("key", apiKey)
                .queryParam("domain", apiDomain)
                .queryParam("data", DATA_ID)
                .queryParam("format", "json")
                .queryParam("size", "1000") // 한번에 가져올 수 있는 최대값
                .queryParam("geomFilter", geomFilter)
                .build()
                .toUri();

        log.info("Fetching CCTV data from V-World: {}", uri);

        try {
            CctvApiResponse response = restTemplate.getForObject(uri, CctvApiResponse.class);

            if (response == null || response.response() == null || response.response().result() == null ||
                    !"OK".equalsIgnoreCase(response.response().status())) {
                log.warn("No data or non-OK status received from V-World API. Status: {}",
                        response != null ? response.response().status() : "NULL");
                return List.of();
            }

            return response.response().result().featureCollection().features();

        } catch (Exception e) {
            log.error("Failed to fetch CCTV data from API.", e);
            return List.of();
        }
    }

    // 원형 반경을 사각형 경계 박스로 변환하는 Record
    private record BoxCoordinates(double minLat, double minLng, double maxLat, double maxLng) {}


     //중심 좌표와 반경을 기반으로 경계 BOX 좌표를 계산

    private BoxCoordinates calculateBoundingBox(double centerLat, double centerLng, double radiusKm) {
        final double LAT_DEGREE_PER_KM = 0.009009;
        final double LNG_DEGREE_PER_KM = 0.0112;

        double latDelta = radiusKm * LAT_DEGREE_PER_KM;
        double lngDelta = radiusKm * LNG_DEGREE_PER_KM / Math.cos(Math.toRadians(centerLat));

        return new BoxCoordinates(
                centerLat - latDelta,
                centerLng - lngDelta,
                centerLat + latDelta,
                centerLng + lngDelta
        );
    }
}