package org.test.reviewservice.repository;


import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.test.reviewservice.entity.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

   List<Review> findAllByProductIdOrderByCreatedAtDesc(Long productId);

    List<Review> findAllByProductIdAndIdGreaterThanOrderByCreatedAtDesc(Long productId, Long reviewId, Pageable pageable);

    Review findByUserIdAndProductId(@NotNull Long userId, Long id);

    List<Review> findAllByProductId(Long id);


}
