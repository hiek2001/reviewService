package org.test.reviewservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.test.reviewservice.dto.ImageRequestDto;
import org.test.reviewservice.dto.ProductReviewRequestDto;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review", indexes = {@Index(name = "created_at_index", columnList = "created_at")})
public class Review extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private float score;

    @Column(length = 50000, nullable = false)
    @NotBlank(message = "리뷰 내용은 필수 입니다.")
    private String content;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    public Review(ProductReviewRequestDto reviewRequestDto, Product product, String imageFileName) {
        this.userId = reviewRequestDto.getUserId();
        this.score = reviewRequestDto.getScore();
        this.content = reviewRequestDto.getContent();
        this.imageUrl = imageFileName;
        this.product = product;
    }

}
