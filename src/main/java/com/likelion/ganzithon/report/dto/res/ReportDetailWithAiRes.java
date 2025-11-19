package com.likelion.ganzithon.report.dto.res;

import com.likelion.ganzithon.report.domain.Report;
import com.likelion.ganzithon.report.domain.ReportAnalysis;

public record ReportDetailWithAiRes(
        ReportRes report,
        AiAnalysisRes aiAnalysis
) {
    public static ReportDetailWithAiRes of(Report report, ReportAnalysis analysis) {
        return new ReportDetailWithAiRes(
                ReportRes.from(report),
                AiAnalysisRes.from(analysis)
        );
    }
}
