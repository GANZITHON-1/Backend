package com.likelion.ganzithon.publicdata.complaint.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ComplaintApiResponse(

        @JsonProperty("header")
        Header header,

        @JsonProperty("body")
        Body body
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Header(
            @JsonProperty("resultCode")
            String resultCode,
            @JsonProperty("resultMsg")
            String resultMsg
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Body(
            @JsonProperty("items")
            Items items,
            @JsonProperty("totalCount")
            String totalCount
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Items(
            @JsonProperty("item")
            List<Item> item
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            @JsonProperty("create_dt")
            String createDt,
            @JsonProperty("event_lat")
            String eventLat,
            @JsonProperty("event_lon")
            String eventLon
    ) {}
}
