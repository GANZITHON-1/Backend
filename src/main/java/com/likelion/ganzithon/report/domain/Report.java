package com.likelion.ganzithon.report.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    private String roadAddress;
    private String lotAddress;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType sourceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Report create(
            Long userId,
            String title,
            String description,
            String imageUrl,
            String roadAddress,
            String lotAddress,
            Double latitude,
            Double longitude,
            SourceType sourceType
    ) {
        Report report = new Report();
        report.userId = userId;
        report.title = title;
        report.description = description;
        report.imageUrl = imageUrl;
        report.roadAddress = roadAddress;
        report.lotAddress = lotAddress;
        report.latitude = latitude;
        report.longitude = longitude;
        report.sourceType = sourceType;
        report.status = ReportStatus.PENDING; // 최초 생성 무조건 PENDING
        return report;
    }

    public void update(String title, String description, String imageUrl) {

        if(title != null && !title.isBlank()) {
            this.title = title;
        }
        if(description != null && !description.isBlank()) {
            this.description = description;
        }
        if(imageUrl != null && !imageUrl.isBlank()) {
            this.imageUrl = imageUrl;
        }
    }

    // AI 분석 완료 시 상태 변경
    public void markCompleted() {
        this.status = ReportStatus.COMPLETED;
    }
}
