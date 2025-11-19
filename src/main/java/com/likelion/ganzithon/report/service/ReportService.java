package com.likelion.ganzithon.report.service;

import com.likelion.ganzithon.exception.CustomException;
import com.likelion.ganzithon.exception.status.ErrorStatus;
import com.likelion.ganzithon.report.domain.Report;
import com.likelion.ganzithon.report.dto.req.ReportCreateReq;
import com.likelion.ganzithon.report.dto.req.ReportUpdateReq;
import com.likelion.ganzithon.report.dto.res.ReportRes;
import com.likelion.ganzithon.report.dto.res.ReportUpdateRes;
import com.likelion.ganzithon.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;

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

    @Transactional(readOnly = true)
    public ReportRes get(Long id) {

        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorStatus.REPORT_NOT_FOUND));

        return ReportRes.from(report);
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
