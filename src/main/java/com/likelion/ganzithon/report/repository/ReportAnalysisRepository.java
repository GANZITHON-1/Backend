package com.likelion.ganzithon.report.repository;

import com.likelion.ganzithon.report.domain.ReportAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportAnalysisRepository extends JpaRepository<ReportAnalysis, Long> {

    Optional<ReportAnalysis> findByReport_Id(Long reportId);
}
