package com.likelion.ganzithon.publicdata.traffic.service;

import com.likelion.ganzithon.publicdata.traffic.dto.TrafficApiResponse;
import com.likelion.ganzithon.publicdata.traffic.dto.TrafficApiResponse.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficApiCaller {

    private final RestTemplate restTemplate;

    @Value("${public.api.traffic-url}")
    private String apiUrl;

    @Value("${public.api.traffic-key}")
    private String authKey;

    private static final String DEFAULT_YEAR = "2024";

    public List<Item> fetchTrafficDataByRegion(String siDo, String guGun) {

        URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("authKey", authKey)
                .queryParam("searchYearCd", DEFAULT_YEAR)
                .queryParam("siDo", siDo)
                .queryParam("guGun", guGun)
                .queryParam("type", "json")
                .queryParam("numOfRows", 100)
                .encode(StandardCharsets.UTF_8)
                .build(true)
                .toUri();

        log.info("Traffic API 호출 URI: {}", uri);

        try {
            TrafficApiResponse response = restTemplate.getForObject(uri, TrafficApiResponse.class);

            if (response == null || !"00".equals(response.resultCode())) {
                return Collections.emptyList();
            }

            List<Item> rawItems = response.items() != null && response.items().item() != null
                    ? response.items().item()
                    : Collections.emptyList();

            return rawItems.stream()
                    .map(item -> new Item(
                            item.afosFid(),
                            item.afosId(),
                            item.bjdCd(),
                            item.spotCd(),
                            item.sidoSggNm(),
                            item.spotNm(),
                            item.occrrncCnt(),
                            item.casltCnt(),
                            item.dthDnvCnt(),
                            item.seDnvCnt(),
                            item.slDnvCnt(),
                            item.wndDnvCnt(),
                            convertToDouble(item.loCrd().toString()),
                            convertToDouble(item.laCrd().toString()),
                            item.geomJson()
                    ))
                    .filter(item -> item.loCrd() != null && item.laCrd() != null)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Traffic API 호출 실패", e);
            return Collections.emptyList();
        }
    }

    private Double convertToDouble(String value) {
        if (value == null || value.isEmpty()) return null;
        try { return Double.parseDouble(value); }
        catch (NumberFormatException e) {
            log.error("좌표 변환 실패: {}", value, e);
            return null;
        }
    }
}