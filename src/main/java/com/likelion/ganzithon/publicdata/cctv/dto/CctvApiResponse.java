package com.likelion.ganzithon.publicdata.cctv.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// 전체 응답 감싸는 Wrapper
@JsonIgnoreProperties(ignoreUnknown = true)
public record CctvApiResponse(
        Response response
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            String status,
            Result result
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            FeatureCollection featureCollection
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record FeatureCollection(
            List<Feature> features
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Feature(
            String id,         
            Geometry geometry,
            Properties properties
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(
            String type,
            List<Double> coordinates
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Properties(
            @JsonProperty("locate")
            String locate,
            @JsonProperty("cctvname")
            String cctvName
    ) {}
}