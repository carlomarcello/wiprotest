package com.cmarcello.wiprotest.repositories;

import com.cmarcello.wiprotest.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql({"/products_test_data.sql"})
class ProductRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ProductRepository productRepository;

    int productCount;

    @BeforeEach
    void setup() {
        productCount = (int) productRepository.count();
    }

    @Test
    void testfindAll() {
        PageRequest pageRequest = PageRequest.of(0, productCount + 1);
        List<Product> products = productRepository.findAll(pageRequest).getContent();
        assertEquals(productCount, products.size());

        pageRequest = PageRequest.of(1, 5);
        products = productRepository.findAll(pageRequest).getContent();
        assertEquals(5, products.size());
    }

    @Test
    void testfindAllByActive() {
        PageRequest pageRequest = PageRequest.of(0, productCount);

        List<Product> products = productRepository.findAllByActive(true, pageRequest);
        int activeProducts = products.size();

        products = productRepository.findAllByActive(false, pageRequest);
        int inactiveProducts = products.size();

        assertNotEquals(0, activeProducts);
        assertNotEquals(0, inactiveProducts);
        assertEquals(productCount, activeProducts + inactiveProducts);
    }
}