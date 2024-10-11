package org.test.reviewservice.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@RequiredArgsConstructor
public class ProductReviewRequestDto {

    @NotNull
    private final Long userId;

    @NotNull
    private final float score;

    @NotBlank
    private final String content;

    private final MultipartFile imageFile;

}
