package com.likelion.ganzithon.map.service;

import com.likelion.ganzithon.map.dto.RegionCodeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RegionService {

    public RegionCodeDto getRegionCodesByCoordinates(double lat, double lng) {

        // 서울특별시
        if (lat >= 37.4 && lat <= 37.7 && lng >= 126.8 && lng <= 127.2) {
            return new RegionCodeDto("11", "680"); // 서울 강남구
        }

        // 부산광역시
        else if (lat >= 35.0 && lat <= 35.3 && lng >= 129.0 && lng <= 129.2) {
            return new RegionCodeDto("26", "350"); // 부산 해운대구
        }

        // 대구광역시
        else if (lat >= 35.8 && lat <= 35.9 && lng >= 128.5 && lng <= 128.7) {
            return new RegionCodeDto("27", "110"); // 대구 중구
        }

        // 인천광역시
        else if (lat >= 37.4 && lat <= 37.5 && lng >= 126.6 && lng <= 126.8) {
            return new RegionCodeDto("28", "120"); // 인천 연수구
        }

        // 광주광역시
        else if (lat >= 35.1 && lat <= 35.2 && lng >= 126.8 && lng <= 127.0) {
            return new RegionCodeDto("29", "140"); // 광주 동구
        }

        // 대전광역시
        else if (lat >= 36.3 && lat <= 36.4 && lng >= 127.3 && lng <= 127.4) {
            return new RegionCodeDto("30", "150"); // 대전 중구
        }

        // 울산광역시
        else if (lat >= 35.5 && lat <= 35.6 && lng >= 129.3 && lng <= 129.4) {
            return new RegionCodeDto("31", "160"); // 울산 남구
        }

        // 세종특별자치시
        else if (lat >= 36.4 && lat <= 36.5 && lng >= 127.2 && lng <= 127.3) {
            return new RegionCodeDto("36", "170"); // 세종
        }

        // 경기도 (수원시 장안구)
        else if (lat >= 37.2 && lat <= 37.3 && lng >= 127.0 && lng <= 127.1) {
            return new RegionCodeDto("41", "310"); // 수원 장안구
        }

        // 경상남도 (창원시 의창구)
        else if (lat >= 35.2 && lat <= 35.3 && lng >= 128.5 && lng <= 128.6) {
            return new RegionCodeDto("42", "330"); // 창원 의창구
        }

        // 경상북도 (포항시 남구)
        else if (lat >= 36.0 && lat <= 36.1 && lng >= 129.3 && lng <= 129.4) {
            return new RegionCodeDto("43", "390"); // 포항 남구
        }

        log.warn("일치하는 지역 코드를 찾지 못해 기본값(서울 강남구: 11, 680)을 반환합니다.");
        return new RegionCodeDto("11", "680");
    }
}
