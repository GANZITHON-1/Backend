package com.likelion.ganzithon.map.service;

import com.likelion.ganzithon.map.dto.RegionCodeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {

    public List<RegionCodeDto> getRegionCodesByCoordinatesList(double lat, double lng) {
        return List.of(new RegionCodeDto("11", "680")); // "11"은 서울시, "680"은 강남구 예시 코드
    }

    public List<RegionCodeDto> getAllGuGunsBySiDo(String siDo) {
            if ("11".equals(siDo)) { // 서울특별시 시도 코드
                return List.of(
                        new RegionCodeDto("11", "000"), // 종로구 (000은 시군구 미지정/전체를 의미할 수도 있지만, 여기서는 종로구를 대표 코드로 사용)
                        new RegionCodeDto("11", "000"), // 중구
                        new RegionCodeDto("11", "110"), // 용산구
                        new RegionCodeDto("11", "140"), // 성동구
                        new RegionCodeDto("11", "170"), // 광진구
                        new RegionCodeDto("11", "200"), // 동대문구
                        new RegionCodeDto("11", "210"), // 중랑구
                        new RegionCodeDto("11", "230"), // 성북구
                        new RegionCodeDto("11", "260"), // 강북구
                        new RegionCodeDto("11", "290"), // 도봉구
                        new RegionCodeDto("11", "320"), // 노원구
                        new RegionCodeDto("11", "350"), // 은평구
                        new RegionCodeDto("11", "380"), // 서대문구
                        new RegionCodeDto("11", "410"), // 마포구
                        new RegionCodeDto("11", "440"), // 양천구
                        new RegionCodeDto("11", "470"), // 강서구
                        new RegionCodeDto("11", "500"), // 구로구
                        new RegionCodeDto("11", "530"), // 금천구
                        new RegionCodeDto("11", "545"), // 영등포구
                        new RegionCodeDto("11", "590"), // 동작구
                        new RegionCodeDto("11", "620"), // 관악구
                        new RegionCodeDto("11", "650"), // 서초구
                        new RegionCodeDto("11", "680"), // 강남구
                        new RegionCodeDto("11", "710"), // 송파구
                        new RegionCodeDto("11", "740")  // 강동구
                );
            }

        return List.of();
    }
}