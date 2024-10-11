package org.test.reviewservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.test.reviewservice.dto.ProductReviewRequestDto;
import org.test.reviewservice.dto.SliceResponseDto;
import org.test.reviewservice.service.ProductReviewService;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/products")
public class ProductController {

    private final ProductReviewService productReviewService;

    @GetMapping("/{productId}/reviews")
    public SliceResponseDto retrieveReviewsForProduct(
            @PathVariable Long productId,
            @RequestParam(value = "cursor", required = false, defaultValue = "0") Long cursor ,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        return productReviewService.retrieveReviewsForProduct(productId, cursor, size);
    }

    @PostMapping("/{productId}/reviews")
    public void createReviewToProduct(@PathVariable Long productId, @Valid ProductReviewRequestDto reviewDto) {
        productReviewService.createReviewToProduct(productId, reviewDto);
    }
}
