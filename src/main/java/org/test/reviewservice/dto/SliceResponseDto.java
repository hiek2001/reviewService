package org.test.reviewservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.test.reviewservice.entity.Product;


import java.util.List;

@Getter
@NoArgsConstructor
public class SliceResponseDto {
    private Long totalCount;
    private float score;
    private Long cursor;
    private List<ReviewResponseDto> reviews;

    public SliceResponseDto(Product product, Long cursor, List<ReviewResponseDto> reviews) {
        this.totalCount = product.getReviewCount();
        this.score = product.getScore();
        this.cursor = cursor;
        this.reviews = reviews;
    }
}
