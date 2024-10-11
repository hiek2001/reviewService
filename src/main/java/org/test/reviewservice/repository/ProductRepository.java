package org.test.reviewservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.reviewservice.entity.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
