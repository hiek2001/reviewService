package org.test.reviewservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.test.reviewservice.entity.Product;
import org.test.reviewservice.entity.Review;
import org.test.reviewservice.repository.ProductRepository;
import org.test.reviewservice.repository.ReviewRepository;
import org.test.reviewservice.service.ProductReviewService;

import java.util.List;

@Slf4j(topic = "Scheduler")
@Component
@RequiredArgsConstructor
public class Scheduler {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductReviewService productReviewService;

    // 리뷰 등록이 거의 없는 매일 새벽 시간에 product 리뷰 갯수와 점수 업데이트 실행
    @Scheduled(cron = "0 0 1 * * *")
    public void updateReviewCountAndScore() throws InterruptedException {
        log.info("Product 리뷰 갯수, 점수 업데이트 실행");

        List<Product> productList = productRepository.findAll();

        for (Product product : productList) {
            List<Review> reviewList = reviewRepository.findAllByProductId(product.getId());

            if(!reviewList.isEmpty()) {
                Long id = product.getId();

                try {
                    productReviewService.updateByProduct(id, reviewList);
                } catch (Exception e) {
                    log.error(id+" : "+e.getMessage());
                }

            }
        }
    }
}
