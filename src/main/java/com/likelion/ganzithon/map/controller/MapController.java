package com.likelion.ganzithon.map.controller;

import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.exception.status.SuccessStatus;
import com.likelion.ganzithon.map.dto.MarkerDto;
import com.likelion.ganzithon.map.dto.PublicMarkerDetailDto;
import com.likelion.ganzithon.map.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/map")
@Tag(name = "Map API", description = "지도 마커 관련 API")
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @Operation(summary = "마커 목록 조회", description = "필터에 따라 지도 마커를 조회합니다.")
    @GetMapping("/markers")
    public Response<List<MarkerDto>> getMarkers(
            @RequestParam List<String> filters,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radius
    ) {
        return Response.success(SuccessStatus.MAP_MARKER_SUCCESS,
                mapService.getMarkers(filters, lat, lng, radius));
    }

    @Operation(summary = "공공 데이터 마커 상세 조회", description = "특정 공공 데이터 마커(PUBLIC)의 상세 정보를 ID를 통해 조회합니다.")
    @GetMapping("/markers/public/{id}")
    public Response<PublicMarkerDetailDto> getPublicMarkerDetail(
            @Parameter(description = "조회할 공공 데이터 마커의 고유 ID", example = "2000101")
            @PathVariable Long id
    ) {
        return Response.success(SuccessStatus.MAP_PUBLIC_DETAIL,
                mapService.getPublicMarkerDetail(id));
    }
}