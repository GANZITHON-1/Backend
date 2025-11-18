package com.likelion.ganzithon.report.controller;

import com.likelion.ganzithon.auth.entity.User;
import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.exception.status.SuccessStatus;
import com.likelion.ganzithon.report.dto.req.ReportCreateReq;
import com.likelion.ganzithon.report.dto.req.ReportUpdateReq;
import com.likelion.ganzithon.report.dto.res.ReportRes;
import com.likelion.ganzithon.report.dto.res.ReportUpdateRes;
import com.likelion.ganzithon.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReportController implements ReportApi{

    private final ReportService reportService;

    @Override
    public Response<ReportRes> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReportCreateReq req) {
        return Response.success(SuccessStatus.REPORT_CREATED, reportService.create(user.getUserId(), req));
    }

    @Override
    public Response<ReportRes> get(@PathVariable Long id) {
        return Response.success(SuccessStatus.SUCCESS, reportService.get(id));
    }

    @Override
    public Response<ReportUpdateRes> update(
            @PathVariable Long id,
            @RequestBody ReportUpdateReq req
    ) {
        ReportUpdateRes res = reportService.update(id, req);
        return Response.success(SuccessStatus.REPORT_UPDATED, res);
    }

    @Override
    public Response<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return Response.success(SuccessStatus.REPORT_DELETED, null);
    }
}
