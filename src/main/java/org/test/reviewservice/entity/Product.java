package org.test.reviewservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long id;

    @Column(name = "reviewCount")
    private Long reviewCount;

    @Column(name = "score")
    private float score;

    public Product(float newScore, Long size) {
        this.reviewCount = size;
        this.score = newScore;
    }


    public void calculateScoreAndReviewCount(List<Review> reviews) {
        int sum = 0;
        for(Review review : reviews) {
            sum += review.getScore();
        }
        this.score = sum / reviews.size();
        this.reviewCount = (long) reviews.size();
    }
}
