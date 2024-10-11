package org.test.reviewservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.test.reviewservice.entity.Review;

@Getter
@NoArgsConstructor
public class ImageRequestDto {
    private MultipartFile file;
    private String fileName;

}
