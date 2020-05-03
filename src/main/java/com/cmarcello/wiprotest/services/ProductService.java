package com.cmarcello.wiprotest.services;

import com.cmarcello.wiprotest.web.model.ProductDto;
import com.github.fge.jsonpatch.JsonPatch;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto getById(UUID productId);
    ProductDto save(ProductDto productDto);
    ProductDto update(UUID productId, ProductDto productDto);
    ProductDto update(UUID productId, JsonPatch jsonPatch);
    List<ProductDto> findAll(PageRequest pageRequest);
    List<ProductDto> findAll(Boolean active, PageRequest pageRequest);
}
