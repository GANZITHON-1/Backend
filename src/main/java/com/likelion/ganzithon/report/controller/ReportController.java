package com.likelion.ganzithon.report.controller;

import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.exception.status.SuccessStatus;
import com.likelion.ganzithon.report.dto.req.ReportCreateReq;
import com.likelion.ganzithon.report.dto.req.ReportUpdateReq;
import com.likelion.ganzithon.report.dto.res.ReportRes;
import com.likelion.ganzithon.report.dto.res.ReportUpdateRes;
import com.likelion.ganzithon.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public Response<ReportRes> create(@Valid @RequestBody ReportCreateReq req) {
        return Response.success(SuccessStatus.REPORT_CREATED, reportService.create(req));
    }

    @GetMapping("/{id}")
    public Response<ReportRes> get(@PathVariable Long id) {
        return Response.success(SuccessStatus.SUCCESS, reportService.get(id));
    }

    @PutMapping("/{id}")
    public Response<ReportUpdateRes> update(
            @PathVariable Long id,
            @RequestBody ReportUpdateReq req
    ) {
        ReportUpdateRes res = reportService.update(id, req);
        return Response.success(SuccessStatus.REPORT_UPDATED, res);
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        reportService.delete(id);
        return Response.success(SuccessStatus.REPORT_DELETED, null);
    }
}
