package com.likelion.ganzithon.report.repository;

import com.likelion.ganzithon.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // rag: 같은 도로명 주소의 최근 신고 5건(현재 신고 제외)
    List<Report> findTop5ByRoadAddressAndIdNotOrderByCreatedAtDesc(String roadAddress, Long excludeId);

}
