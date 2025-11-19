package com.likelion.ganzithon.report.dto.res;

import com.likelion.ganzithon.report.domain.ReportAnalysis;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record AiAnalysisRes(
        String summaryText,
        String severityLevel,
        String aiModelVersion,
        boolean ragEnabled,
        List<Long> retrievedSources,
        Integer contextCount,
        Double confidenceScore,
        String status,
        String generatedAt
) {
    public static AiAnalysisRes from(ReportAnalysis a) {
        List<Long> sources = Arrays.stream(a.getRetrievedSources()
                .replace("[", "")
                .replace("]", "")
                .split(","))
                .filter(s -> !s.isBlank())
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());

        return new AiAnalysisRes(
                a.getSummaryText(),
                a.getSeverityLevel().name(),
                a.getAiModelVersion(),
                a.isRagEnabled(),
                sources,
                a.getContextCount(),
                a.getConfidenceScore().doubleValue(),
                a.getStatus().name(),
                a.getGeneratedAt().toString()
        );
    }
}
