package com.cmarcello.wiprotest.services;

import com.cmarcello.wiprotest.domain.Product;
import com.cmarcello.wiprotest.repositories.ProductRepository;
import com.cmarcello.wiprotest.web.model.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ProductServiceImplTest {

    @TestConfiguration
    @ComponentScan({"com.cmarcello.wiprotest.web.mapper",
                    "com.cmarcello.wiprotest.services"})
    static class ProductServiceImplTestContextConfiguration {
    }

    @Autowired
    ProductServiceImpl productService;

    @MockBean
    ProductRepository productRepository;

    Product product;
    ProductDto productDto;
    PageRequest defaultPageRequest;

    @BeforeEach
    void setup() {
        product = Product.builder()
                .id(UUID.randomUUID())
                .description("PRD1")
                .value(new BigDecimal(99.90))
                .active(true)
                .creationDate(new Timestamp(System.currentTimeMillis()))
                .build();

        productDto = ProductDto.builder()
                .description("PRD1")
                .value(new BigDecimal(99.90))
                .active(true)
                .creationDate(OffsetDateTime.now())
                .build();

        defaultPageRequest = PageRequest.of(0, 5);
    }

    @Test
    void testGetById() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        ProductDto productDto = productService.getById(product.getId());
        assertEquals(product.getId(), productDto.getId());
    }

    @Test
    void testSave() {
        when(productRepository.save(Mockito.any(Product.class))).thenReturn(product);
        ProductDto productDtoReturn = productService.save(productDto);
        assertEquals(product.getId(), productDtoReturn.getId());
    }

    @Test
    void testUpdate() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productDto.setId(product.getId());
        ProductDto productDtoReturn = productService.update(productDto.getId(), productDto);

        assertEquals(productDto.getDescription(), productDtoReturn.getDescription());
        assertEquals(productDto.getValue(), productDtoReturn.getValue());
        assertEquals(productDto.getActive(), productDtoReturn.getActive());
    }

    @Test
    void testFindAllPageableWithoutFilter() {
        List<Product> products = new ArrayList<>();
        Page<Product> pagedResponse = new PageImpl<>(products);
        when(productRepository.findAll(any(Pageable.class))).thenReturn(pagedResponse);

        productService.findAll(defaultPageRequest);

        ArgumentCaptor<Pageable> pageableArgument = ArgumentCaptor.forClass(Pageable.class);
        ArgumentCaptor<Boolean> booleanArgument = ArgumentCaptor.forClass(Boolean.class);

        verify(productRepository, times(1)).findAll(pageableArgument.capture());
        verify(productRepository, times(0)).findAllByActive(booleanArgument.capture(), pageableArgument.capture());
    }

    @Test
    void testFindAllPageableByActive() {
        List<Product> products = new ArrayList<>();
        when(productRepository.findAllByActive(anyBoolean(), any(Pageable.class))).thenReturn(products);

        productService.findAll(true, defaultPageRequest);

        ArgumentCaptor<Pageable> pageableArgument = ArgumentCaptor.forClass(Pageable.class);
        ArgumentCaptor<Boolean> booleanArgument = ArgumentCaptor.forClass(Boolean.class);

        verify(productRepository, times(0)).findAll(pageableArgument.capture());
        verify(productRepository, times(1)).findAllByActive(booleanArgument.capture(), pageableArgument.capture());
    }
}