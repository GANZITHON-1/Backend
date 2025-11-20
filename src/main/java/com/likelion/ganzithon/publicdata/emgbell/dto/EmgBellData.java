package com.likelion.ganzithon.publicdata.emgbell.dto;

public record EmgBellData(
        String objectId, // 시설 번호(고유 id)
        String facilityType,  // 시설 유형
        String managingInstitution,  // 관리 기관(경찰서 등)
        String installationPurpose, // 설치 목적
        String installationType,  // 설치 장소 유형(화장실 등)
        String installationDetail, // 설치 위치 상세
        String roadAddress,  // 도로명 주소
        String lotAddress,    // 지번 주소
        double latitude,      // 위도
        double longitude       // 경도
) {
}
