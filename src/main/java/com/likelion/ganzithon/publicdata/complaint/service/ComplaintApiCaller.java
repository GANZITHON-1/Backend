package com.likelion.ganzithon.publicdata.complaint.service;

import com.likelion.ganzithon.publicdata.complaint.dto.ComplaintApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComplaintApiCaller {

    private final RestTemplate restTemplate;

    @Value("${public.api.complaint-url}")
    private String apiUrl;

    @Value("${public.api.complaint-key}")
    private String apiKey;

    public List<ComplaintApiResponse.Item> fetchComplaintItems() {

        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 10);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");

        List<ComplaintApiResponse.Item> allItems = new ArrayList<>();

        LocalDate cursor = start;

        while (!cursor.isAfter(end)) {
            LocalDate sliceEnd = cursor.plusDays(14);
            if (sliceEnd.isAfter(end)) {
                sliceEnd = end;
            }

            String dateFrom = cursor.format(fmt);
            String dateTo = sliceEnd.format(fmt);

            URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("serviceKey", apiKey)
                    .queryParam("dateFrom", dateFrom)
                    .queryParam("dateTo", dateTo)
                    .build(true)
                    .toUri();

            log.info("Complaint API URI ({} ~ {}) = {}", dateFrom, dateTo, uri);

            try {
                ComplaintApiResponse response =
                        restTemplate.getForObject(uri, ComplaintApiResponse.class);

                if (response == null
                        || response.body() == null
                        || response.body().items() == null
                        || response.body().items().item() == null) {
                    log.warn("Complaint API returned empty body/items for {} ~ {}", dateFrom, dateTo);
                } else {
                    List<ComplaintApiResponse.Item> items = response.body().items().item();
                    log.info("Complaint API items size for {} ~ {} = {}", dateFrom, dateTo, items.size());
                    allItems.addAll(items);
                }

            } catch (Exception e) {
                log.error("Complaint API error ({} ~ {})", dateFrom, dateTo, e);
            }

            cursor = sliceEnd.plusDays(1);
        }

        log.info("Complaint API total items for 2024 = {}", allItems.size());

        if (!allItems.isEmpty()) {
            ComplaintApiResponse.Item first = allItems.get(0);
            log.info("First complaint item: create_dt={}, lat={}, lon={}",
                    first.createDt(), first.eventLat(), first.eventLon());
        }

        return Collections.unmodifiableList(allItems);
    }
}
