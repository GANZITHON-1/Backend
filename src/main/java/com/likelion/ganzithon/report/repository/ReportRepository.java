package com.likelion.ganzithon.report.repository;

import com.likelion.ganzithon.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
