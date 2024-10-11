package org.test.reviewservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.test.reviewservice.entity.Product;
import org.test.reviewservice.entity.Review;
import org.test.reviewservice.repository.ProductRepository;
import org.test.reviewservice.repository.ReviewRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ProductReviewServiceTest {


    @Test
    @DisplayName("사진 정상 업로드 테스트")
    public void testUploadImage() throws Exception {
        // given : 가짜 multipartFile 만들기
        MockMultipartFile imageFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());

        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + imageFile.getOriginalFilename();

        // when
        String uploadFolder = System.getProperty("java.io.tmpdir");
        Path imageFilePath = Paths.get(uploadFolder,imageFileName);
        try {
            Files.write(imageFilePath, imageFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // then
        assertNotNull(imageFile, "Image file name should not be null");
        assertTrue(imageFileName.contains("test.jpg"));
        assertTrue(Files.exists(imageFilePath));

        Files.delete(imageFilePath);
    }

}
