package com.likelion.ganzithon.map.dto;

public record RegionCodeDto(
        String siDo,   // 시도 코드
        String guGun   // 시군구 코드

) {
    // Optional: 명시적으로 getter 만들고 싶다면
    public String getSiDo() {
        return siDo;
    }

    public String getGuGun() {
        return guGun;
    }
}
