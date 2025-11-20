package com.likelion.ganzithon.publicdata.traffic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TrafficApiResponse(
        @JsonProperty("resultCode") String resultCode,
        @JsonProperty("resultMsg") String resultMsg,
        @JsonProperty("totalCount") Integer totalCount,
        @JsonProperty("numOfRows") Integer numOfRows,
        @JsonProperty("pageNo") Integer pageNo,
        @JsonProperty("items") ItemsWrapper items
) {
    public record ItemsWrapper(
            @JsonProperty("item") List<Item> item // 실제 데이터 목록
    ) {}

    public record Item(
            @JsonProperty("afos_fid") String afosFid,
            @JsonProperty("afos_id") String afosId,
            @JsonProperty("bjd_cd") String bjdCd,
            @JsonProperty("spot_cd") String spotCd,
            @JsonProperty("sido_sgg_nm") String sidoSggNm,
            @JsonProperty("spot_nm") String spotNm,
            @JsonProperty("occrrnc_cnt") Integer occrrncCnt,
            @JsonProperty("caslt_cnt") Integer casltCnt,
            @JsonProperty("dth_dnv_cnt") Integer dthDnvCnt,
            @JsonProperty("se_dnv_cnt") Integer seDnvCnt,
            @JsonProperty("sl_dnv_cnt") Integer slDnvCnt,
            @JsonProperty("wnd_dnv_cnt") Integer wndDnvCnt,
            @JsonProperty("lo_crd") Double loCrd,            // 경도
            @JsonProperty("la_crd") Double laCrd,            // 위도
            @JsonProperty("geom_json") String geomJson
    ) {}
}