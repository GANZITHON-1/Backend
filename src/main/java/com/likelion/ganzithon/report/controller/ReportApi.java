package com.likelion.ganzithon.report.controller;

import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.report.dto.req.ReportCreateReq;
import com.likelion.ganzithon.report.dto.req.ReportUpdateReq;
import com.likelion.ganzithon.report.dto.res.ReportRes;
import com.likelion.ganzithon.report.dto.res.ReportUpdateRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Report API", description = "사용자 사고 등록 API ")
@RequestMapping("/report")
public interface ReportApi {

    @Operation(summary = "신고 생성", description = "신고 데이터를 생성합니다.")
    @PostMapping
    Response<ReportRes> create(@Valid @RequestBody ReportCreateReq req);

    @Operation(summary = "신고 상세 조회", description = "신고 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    Response<ReportRes> get(@PathVariable Long id);

    @Operation(summary = "신고 수정", description = "신고 내용을 수정합니다.")
    @PutMapping("/{id}")
    Response<ReportUpdateRes> update(
            @PathVariable Long id,
            @RequestBody ReportUpdateReq req
    );

    @Operation(summary = "신고 삭제", description = "신고 데이터를 삭제합니다.")
    @DeleteMapping("/{id}")
    Response<Void> delete(@PathVariable Long id);
}
