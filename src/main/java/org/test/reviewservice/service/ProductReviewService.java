package org.test.reviewservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.reviewservice.dto.ProductReviewRequestDto;
import org.test.reviewservice.dto.ReviewResponseDto;
import org.test.reviewservice.dto.SliceResponseDto;
import org.test.reviewservice.entity.Product;
import org.test.reviewservice.entity.Review;
import org.test.reviewservice.repository.ProductRepository;
import org.test.reviewservice.repository.ReviewRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    // productId와 userId를 조합하여 중복 리뷰 방지하는 Map
    private final ConcurrentHashMap<String, Boolean> reviewSubmissionLock = new ConcurrentHashMap<>();

    // 상품에 대한 전체 리뷰 조회
    public SliceResponseDto retrieveReviewsForProduct(Long productId, Long cursor, int size) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new NullPointerException("Product not found"));

        Pageable pageable = PageRequest.of(0, size);

        if(cursor < 1) {
           List<Review> allReviews =  reviewRepository.findAllByProductIdOrderByCreatedAtDesc(productId);
           cursor = (long) allReviews.size() - pageable.getPageSize();
        }

        List<Review> reviews = reviewRepository.findAllByProductIdAndIdGreaterThanOrderByCreatedAtDesc(productId, cursor, pageable);

        // 요구 사항이 score는 float 형식이므로 float로 형 변환
        if(!reviews.isEmpty()) {
            float newScore = (float) reviews.stream()
                    .mapToDouble(Review::getScore)
                    .average()
                    .orElse(0.0);

            newScore = Math.round(newScore * 10) / 10.0f;
            Long newSize = (long) reviews.size();
            product = new Product(newScore, newSize);
        }

        List<ReviewResponseDto> reviewDtoList = reviews.stream()
                .map(review -> new ReviewResponseDto(
                        review.getId(),
                        review.getUserId(),
                        review.getScore(),
                        review.getContent(),
                        review.getImageUrl(),
                        review.getCreatedAt()))
                .collect(Collectors.toList());


        return new SliceResponseDto(product, cursor, reviewDtoList);
    }


    // 리뷰 등록
    public void createReviewToProduct(Long productId, ProductReviewRequestDto reviewDto) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new NullPointerException("Product not found"));

        float score = reviewDto.getScore();
        if(score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }

        String content = reviewDto.getContent();
        if(content == null || content.isEmpty()) { // front에서 걸러주지 못함을 위한 것
            throw new NullPointerException("리뷰의 내용이 없습니다.");
        }

        String userProductKey = reviewDto.getUserId() + "_" + productId;
        if(reviewSubmissionLock.putIfAbsent(userProductKey, true) != null) { // 이미 리뷰를 작성 중인 경우 처리
            throw new IllegalArgumentException("User is already submitting a review for this product");
        }

        Review getReviewsByUser = reviewRepository.findByUserIdAndProductId(reviewDto.getUserId(), product.getId());
        if(getReviewsByUser != null) {
            throw new IllegalArgumentException("User already exists");
        }

        try {
            MultipartFile imageFile = reviewDto.getImageFile();
            String imageFileName = imageFile.getOriginalFilename();

            if(imageFileName != null) {
                uploadImage(imageFile);
            }

            reviewRepository.save(new Review(reviewDto, product, imageFileName));
        } finally {
            // 리뷰 저장이 끝나면 Map에서 제거
            reviewSubmissionLock.remove(userProductKey);
        }

    }

    // 사진 업로드
    public void uploadImage(MultipartFile imageFile) {
        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + imageFile.getOriginalFilename();

        String uploadFolder = System.getProperty("java.io.tmpdir");
        Path imageFilePath = Paths.get(uploadFolder,imageFileName);
        try {
            Files.write(imageFilePath, imageFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void updateByProduct(Long productId, List<Review> reviews) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new NullPointerException("해당 상품이 존재하지 않습니다.")
        );

        product.calculateScoreAndReviewCount(reviews);
    }

}
