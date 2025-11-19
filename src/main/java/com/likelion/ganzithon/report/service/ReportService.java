package com.likelion.ganzithon.report.service;

import com.likelion.ganzithon.exception.CustomException;
import com.likelion.ganzithon.exception.status.ErrorStatus;
import com.likelion.ganzithon.report.domain.Report;
import com.likelion.ganzithon.report.domain.ReportAnalysis;
import com.likelion.ganzithon.report.dto.req.ReportCreateReq;
import com.likelion.ganzithon.report.dto.req.ReportUpdateReq;
import com.likelion.ganzithon.report.dto.res.ReportDetailWithAiRes;
import com.likelion.ganzithon.report.dto.res.ReportRes;
import com.likelion.ganzithon.report.dto.res.ReportUpdateRes;
import com.likelion.ganzithon.report.repository.ReportAnalysisRepository;
import com.likelion.ganzithon.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportAnalysisRepository reportAnalysisRepository;
    private final AiAnalysisService aiAnalysisService;

    public ReportRes create(Long userId, ReportCreateReq req) {

        Report report = Report.create(
                userId,
                req.title(),
                req.description(),
                req.imageUrl(),
                req.roadAddress(),
                req.lotAddress(),
                req.latitude(),
                req.longitude(),
                req.sourceType()
        );

        Report saved = reportRepository.save(report);
        return ReportRes.from(saved);
    }

    @Transactional
    public ReportDetailWithAiRes get(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorStatus.REPORT_NOT_FOUND));

        // 기존 분석이 있으면 사용, 없으면 PENDING
        ReportAnalysis analysis = reportAnalysisRepository.findByReport_Id(id)
                .orElseGet(() -> {
                    ReportAnalysis pending = ReportAnalysis.createPending(report, report.getUserId());
                    return reportAnalysisRepository.save(pending);
                });

        // upstage llm + rag 분석
        aiAnalysisService.analyze(report, analysis);

        // 분석 결과 저장
        reportAnalysisRepository.save(analysis);

        return ReportDetailWithAiRes.of(report, analysis);
    }

    public ReportUpdateRes update(Long id, ReportUpdateReq req) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() ->
                        new CustomException(ErrorStatus.REPORT_NOT_FOUND));

        report.update(
                req.title(),
                req.description(),
                req.imageUrl()
        );

        return ReportUpdateRes.from(report);
    }

    public void delete(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() ->
                        new CustomException(ErrorStatus.REPORT_NOT_FOUND));

        reportRepository.delete(report);
    }
}
