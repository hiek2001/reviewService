package org.test.reviewservice.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.test.reviewservice.entity.Product;
import org.test.reviewservice.entity.Review;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Long userId;
    private float score;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

//    public ReviewResponseDto(Review review, Product product) {
//        this.id = review.getId();
//        this.userId = review.getUserId();
//        this.score = product.getScore();
//        this.content = review.getContent();
//        this.imageUrl = review.getImageUrl();
//        this.createdAt = review.getCreatedAt();
//    }

    public ReviewResponseDto(Long id, Long userId, float score, String content, String imageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.score = score;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }
}
