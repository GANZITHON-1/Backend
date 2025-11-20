package com.likelion.ganzithon.map.controller;

import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.exception.status.SuccessStatus;
import com.likelion.ganzithon.map.dto.MarkerDto;
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

    @Operation(summary = "마커 조회", description = "필터에 따라 지도 마커를 조회합니다.")
    @GetMapping("/markers")
    public Response<List<MarkerDto>> getMarkers(
            @Parameter(description = "필터 목록", example = "complaint")
            @RequestParam List<String> filters,

            @Parameter(description = "현재 위치 위도", example = "37.5665")
            @RequestParam double lat,

            @Parameter(description = "현재 위치 경도", example = "126.9780")
            @RequestParam double lng,

            @Parameter(description = "검색 반경(km)", example = "1.0")
            @RequestParam double radius
    ) {
        return Response.success(SuccessStatus.MAP_MARKER_SUCCESS,
                mapService.getMarkers(filters, lat, lng, radius));
    }
}
