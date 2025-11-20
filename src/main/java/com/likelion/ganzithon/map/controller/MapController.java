package com.likelion.ganzithon.map.controller;

import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.exception.status.SuccessStatus;
import com.likelion.ganzithon.map.dto.MarkerDto;
import com.likelion.ganzithon.map.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
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
            @RequestParam List<String> filters,
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double radius
    ) {
        return Response.success(SuccessStatus.MAP_MARKER_SUCCESS,
                mapService.getMarkers(filters, lat, lng, radius));
    }

}
