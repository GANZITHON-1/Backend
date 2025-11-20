package com.likelion.ganzithon.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CctvService {
    @Value("${public.api.cctv-url}")
    private String baseUrl;
    @Value("${public.api.cctv-key}")
    private String apiKey;
    private final RestTemplate restTemplate;

    public CctvService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public JsonNode getCctvMarkers(double lat, double lon, double radius) {
        try {
            String geomFilter = String.format("BOX(%f,%f,%f,%f)", lon - radius, lat - radius, lon + radius, lat + radius);
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("service", "data")
                    .queryParam("version", "2.0")
                    .queryParam("request", "GetFeature")
                    .queryParam("key", apiKey).queryParam("data", "LT_P_UTISCCTV")
                    .queryParam("format", "json")
                    .queryParam("geomFilter", geomFilter)
                    .queryParam("size", 10).queryParam("page", 1);
            String response = restTemplate.getForObject(uriBuilder.toUriString(), String.class);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(response);
        } catch (Exception e) {
            throw new RuntimeException("CCTV API Fetch Error", e);
        }
    }
}