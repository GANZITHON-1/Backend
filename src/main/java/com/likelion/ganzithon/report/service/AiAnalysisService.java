package com.likelion.ganzithon.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.ganzithon.exception.CustomException;
import com.likelion.ganzithon.exception.status.ErrorStatus;
import com.likelion.ganzithon.report.domain.Report;
import com.likelion.ganzithon.report.domain.ReportAnalysis;
import com.likelion.ganzithon.report.domain.SeverityLevel;
import com.likelion.ganzithon.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiAnalysisService {

    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${upstage.api-key}")
    private String apiKey;

    @Value("${upstage.base-url:https://api.upstage.ai/v1}")
    private String baseUrl;

    @Value("${upstage.model:solar-1-mini}")
    private String model;

    public void analyze(Report report, ReportAnalysis analysis) {
        try {
            // db에서 유사 신고들 조회 (rag)
            List<Report> similarReports = reportRepository
                    .findTop5ByRoadAddressAndIdNotOrderByCreatedAtDesc(
                            report.getRoadAddress(), report.getId()
                    );

            boolean ragEnabled = !similarReports.isEmpty();
            String retrievedSourcesJson = similarReports.stream()
                    .map(r -> String.valueOf(r.getId()))
                    .collect(Collectors.joining(",", "[", "]"));
            int contextCount = similarReports.size();

            // 프롬포트 생성
            String prompt = buildPrompt(report, similarReports);

            // upstage char api 호출
            String url = baseUrl + "/chat/completions";

            String requestBody = """
                    {
                      "model": "%s",
                      "messages": [
                        {
                          "role": "system",
                          "content": "당신은 한국어로 사고 제보 내용을 분석하는 안전 전문가입니다. JSON 형식으로만 응답하세요."
                        },
                        {
                          "role": "user",
                          "content": %s
                        }
                      ],
                      "response_format": {
                        "type": "json_object"
                      }
                    }
                    """.formatted(model, objectMapper.writeValueAsString(prompt));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class
            );

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new CustomException(ErrorStatus.AI_ANALYSIS_ERROR);
            }

            // upstage 응답 파싱
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode contentNode = root
                    .path("choices").get(0)
                    .path("message")
                    .path("content");

            JsonNode result = objectMapper.readTree(contentNode.asText());

            String summaryText = result.path("summaryText").asText();
            String severityKor = result.path("severityLevel").asText("안전");
            double confidence = result.path("confidenceScore").asDouble(0.89);

            SeverityLevel severityLevel = switch (severityKor) {
                case "주의" -> SeverityLevel.주의;
                case "위험" -> SeverityLevel.위험;
                default -> SeverityLevel.안전;
            };

            String modelVersion = root.path("model").asText(model);

            // 엔티티 업데이트 (completed)
            analysis.complete(
                    summaryText,
                    severityLevel,
                    modelVersion,
                    confidence,
                    ragEnabled,
                    retrievedSourcesJson,
                    contextCount
            );
            report.markCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            analysis.fail();
            throw new CustomException(ErrorStatus.AI_ANALYSIS_ERROR);
        }
    }

    private String buildPrompt(Report report, List<Report> similarReports) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String base = """
                [현재 신고]
                - 신고 ID: %d
                - 제목: %s
                - 설명: %s
                - 도로명 주소: %s
                - 지번 주소: %s
                - 위도/경도: %.5f / %.5f
                - 생성일시: %s

                """.formatted(
                report.getId(),
                report.getTitle(),
                report.getDescription(),
                report.getRoadAddress(),
                report.getLotAddress(),
                report.getLatitude(),
                report.getLongitude(),
                report.getCreatedAt() != null ? report.getCreatedAt().format(fmt) : "N/A"
        );

        String context = similarReports.isEmpty()
                ? "유사 신고가 없습니다. 현재 신고만 기반으로 요약 및 위험도를 판단하세요."
                : "[유사 신고 목록]\n" + similarReports.stream()
                .map(r -> "- ID: %d, 제목: %s, 설명: %s, 주소: %s, 생성일시: %s".formatted(
                        r.getId(), r.getTitle(), r.getDescription(),
                        r.getRoadAddress(),
                        r.getCreatedAt() != null ? r.getCreatedAt().format(fmt) : "N/A"
                ))
                .collect(Collectors.joining("\n"));

        String instruction = """
                위 정보를 바탕으로 다음 JSON 형식으로만 응답하세요:

                {
                  "summaryText": "한 문단 이내의 한국어 요약",
                  "severityLevel": "안전" 또는 "주의" 또는 "위험",
                  "confidenceScore": 0.0~1.0 사이의 실수
                }
                """;

        return base + "\n" + context + "\n\n" + instruction;
    }
}
