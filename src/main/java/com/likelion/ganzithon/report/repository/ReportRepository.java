package com.likelion.ganzithon.report.repository;

import com.likelion.ganzithon.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserId(Long userId);
}
