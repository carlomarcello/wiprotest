package com.cmarcello.wiprotest.repositories;

import com.cmarcello.wiprotest.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findAll(Pageable pageable);
    List<Product> findAllByActive(Boolean active, Pageable pageable);
}
