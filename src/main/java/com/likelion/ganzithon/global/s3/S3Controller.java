package com.likelion.ganzithon.global.s3;

import com.likelion.ganzithon.exception.Response;
import com.likelion.ganzithon.exception.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
@Tag(name = "S3 API", description = "S3 API ")
public class S3Controller {

    private final S3Service s3Service;

    @Operation(summary = "이미지 업로드", description = "이미지 파일을 S3에 업로드하고, 접근 가능한 URL을 반환합니다.")
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Response<String> uploadImage(@Parameter(description = "업로드할 이미지 파일", required = true)
                                            @RequestPart("file") @NotNull MultipartFile file) {
        String url = s3Service.uploadImage(file, "reports");
        return Response.success(SuccessStatus.REPORT_CREATED, url);
    }
}
