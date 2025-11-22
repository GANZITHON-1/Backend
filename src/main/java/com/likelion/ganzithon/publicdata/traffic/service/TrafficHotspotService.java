package com.likelion.ganzithon.publicdata.traffic.service;

import com.likelion.ganzithon.map.dto.RegionCodeDto;
import com.likelion.ganzithon.map.service.RegionService;
import com.likelion.ganzithon.publicdata.traffic.dto.TrafficApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficHotspotService {

    private final RegionService regionService;
    private final TrafficApiCaller trafficApiCaller;

    public List<TrafficApiResponse.Item> fetchTrafficHotspots(double lat, double lng, double radiusKm) {
        List<TrafficApiResponse.Item> result = new ArrayList<>();
        List<RegionCodeDto> centralRegions = regionService.getRegionCodesByCoordinatesList(lat, lng);

        if (centralRegions.isEmpty()) {
            return Collections.emptyList();
        }

        String centralSiDo = centralRegions.get(0).siDo();

        List<RegionCodeDto> regionsToSearch = regionService.getAllGuGunsBySiDo(centralSiDo);

        if (regionsToSearch.isEmpty()) {
            log.warn("해당 시도({})에 대한 시군구 코드를 찾을 수 없습니다.", centralSiDo);
            return Collections.emptyList();
        }

        for (RegionCodeDto region : regionsToSearch) {
            List<TrafficApiResponse.Item> items = trafficApiCaller.fetchTrafficDataByRegion(region.siDo(), region.guGun());

            for (TrafficApiResponse.Item item : items) {
                double itemLat = Double.parseDouble(item.laCrd().toString());
                double itemLng = Double.parseDouble(item.loCrd().toString());

                if (isWithinRadius(lat, lng, itemLat, itemLng, radiusKm)) {
                    result.add(item);
                }
            }
        }

        return result;
    }

    private boolean isWithinRadius(double centerLat, double centerLng,
                                   double targetLat, double targetLng, double radiusKm) {
        final int EARTH_RADIUS = 6371;
        double dLat = Math.toRadians(targetLat - centerLat);
        double dLng = Math.toRadians(targetLng - centerLng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(centerLat)) * Math.cos(Math.toRadians(targetLat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;

        return distance <= radiusKm;
    }
}