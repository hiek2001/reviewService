package org.test.reviewservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.test.reviewservice.dto.ProductReviewRequestDto;
import org.test.reviewservice.entity.Product;
import org.test.reviewservice.entity.Review;
import org.test.reviewservice.repository.ProductRepository;
import org.test.reviewservice.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
public class ReviewServiceConcurrenyTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductReviewService productReviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("10명의 사용자가 리뷰를 동시에 작성할 경우, 각자 다른 리뷰를 작성했는지 검증")
    public void testConcurrencyReviewSubmission() throws InterruptedException {
        // 상품 생성 데이터 세팅
        Long productId = 10L;
        Product product = new Product(productId, 0L, 0.0f);
        productRepository.save(product);


        // 10개 스레드 생성
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for(int i=0 ; i<10 ; i++) {
            Long userId = (long) (i+1);

            executorService.submit(() -> {
                try {
                    ProductReviewRequestDto reviewDto = new ProductReviewRequestDto(userId, 4.5f, "Concurrent review", null);

                    productReviewService.createReviewToProduct(productId, reviewDto);
                } catch(IllegalArgumentException e) {
                    System.out.println("Duplicate submission detected :"+ e.getMessage());
                }
            });
        }

        // 스레드가 모두 끝날때까지 대기
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Thread.sleep(1000);


        List<Review> reviewList = reviewRepository.findAllByProductId(productId);

        // 총 10명의 유저가 리뷰를 작성했는지 확인
        assertEquals(10, reviewList.size(), "There should be 10 reviews from different users");

        // 각 유저가 한 번만 리뷰를 작성했는지 확인
        for (int i = 1; i <= 10; i++) {
            Long userId = (long) i;
            long userReviewCount = reviewList.stream()
                    .filter(review -> review.getUserId().equals(userId))
                    .count();

            assertEquals(1, userReviewCount, "User " + userId + " should have only one review for this product");
        }
    }
}
