package com.likelion.ganzithon.publicdata.cctv2.dto; // 패키지 경로를 cctv2.dro로 가정

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record Cctv2ApiResponse(
        Response response
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Response(
            @JsonProperty("data")
            List<CctvItem> data
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CctvItem(
            @JsonProperty("cctvname")
            String cctvName,
            @JsonProperty("cctvurl")
            String cctvUrl,
            @JsonProperty("coordx")
            String coordX, // 경도
            @JsonProperty("coordy")
            String coordY  // 위도
    ) {}
}