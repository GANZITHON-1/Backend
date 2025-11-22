package com.likelion.ganzithon.map.service;

import java.awt.Polygon;

public class RegionGeometry {

    private final String bjdCd;       // 법정동 코드
    private final String sidoSggNm;   // 시군구 이름
    private final String spotCd;      // spot 코드
    private final Polygon polygon;    // 좌표 Polygon

    public RegionGeometry(String bjdCd, String sidoSggNm, String spotCd, Polygon polygon) {
        this.bjdCd = bjdCd;
        this.sidoSggNm = sidoSggNm;
        this.spotCd = spotCd;
        this.polygon = polygon;
    }

    public String getBjdCd() {
        return bjdCd;
    }

    public String getSidoSggNm() {
        return sidoSggNm;
    }

    public String getSpotCd() {
        return spotCd;
    }

    public Polygon getPolygon() {
        return polygon;
    }
}
