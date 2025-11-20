package com.likelion.ganzithon.map.dto;


public record PublicMarkerDetailDto(
        Long markerId,
        String sourceType, // 항상 "PUBLIC"
        String title,
        String filterType,
        String location,
        String severity,
        double latitude,
        double longitude,
        String createdAt
) {
}