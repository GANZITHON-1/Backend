package com.likelion.ganzithon.report.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "report_analysis")
public class ReportAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "summary_text", columnDefinition = "TEXT", nullable = false)
    private String summaryText;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level", nullable = false)
    private SeverityLevel severityLevel;

    @Column(name = "ai_model_version", nullable = false)
    private String aiModelVersion;

    @Column(name = "confidence_score", nullable = false, precision = 4, scale = 2)
    private Double confidenceScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    // RAG 사용 여부
    @Column(name = "rag_enabled", nullable = false)
    private boolean ragEnabled;

    // 참고된 유사 사고 ID 리스트(JSON)
    @Column(name = "retrieved_sources", nullable = false, columnDefinition = "TEXT")
    private String retrievedSources;

    // 참조 문서 개수
    @Column(name = "context_count", nullable = false)
    private Integer contextCount;

    public static ReportAnalysis createPending(Report report, Long userId) {
        ReportAnalysis a = new ReportAnalysis();
        a.report = report;
        a.status = AnalysisStatus.PENDING;
        a.summaryText = "";
        a.severityLevel = SeverityLevel.안전;
        a.aiModelVersion = "";
        a.confidenceScore = 0.0;
        a.generatedAt = LocalDateTime.now();
        a.ragEnabled = false;
        a.retrievedSources = "[]";
        a.contextCount = 0;
        return a;
    }

    public void complete(
            String summaryText,
            SeverityLevel severity,
            String modelVersion,
            Double confidence,
            boolean ragEnabled,
            String retrievedSources,
            Integer contextCount
    ) {
        this.summaryText = summaryText;
        this.severityLevel = severity;
        this.aiModelVersion = modelVersion;
        this.confidenceScore = confidence;
        this.ragEnabled = ragEnabled;
        this.retrievedSources = retrievedSources;
        this.contextCount = contextCount;
        this.generatedAt = LocalDateTime.now();
        this.status = AnalysisStatus.COMPLETED;
    }

    public void fail() {
        this.status = AnalysisStatus.FAILED;
    }
}
