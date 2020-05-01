package com.cmarcello.wiprotest.services;

import com.cmarcello.wiprotest.domain.Product;
import com.cmarcello.wiprotest.repositories.ProductRepository;
import com.cmarcello.wiprotest.web.controller.ResourceNotFoundException;
import com.cmarcello.wiprotest.web.mapper.ProductMapper;
import com.cmarcello.wiprotest.web.model.ProductDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProductDto getById(UUID productId) {
        return productMapper.productToProductDto(productRepository.findById(productId).orElseThrow(ResourceNotFoundException::new));
    }

    @Override
    public ProductDto save(ProductDto productDto) {
        return productMapper.productToProductDto(productRepository.save(productMapper.productDtoToProduct(productDto)));
    }

    @Override
    public ProductDto update(UUID productId, ProductDto productDto) {
        Product product = productRepository.findById(productId).orElseThrow(ResourceNotFoundException::new);

        product.setDescription(productDto.getDescription());
        product.setValue(productDto.getValue());
        product.setActive(productDto.getActive());

        return productMapper.productToProductDto(productRepository.save(product));
    }

    @Override
    public ProductDto update(UUID productId, JsonPatch jsonPatch) {
        try {
            Product product = productRepository.findById(productId).orElseThrow(ResourceNotFoundException::new);
            Product productPatched = applyPatch(jsonPatch, product);
            return productMapper.productToProductDto(productRepository.save(productPatched));
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<ProductDto> findAll(Boolean active, PageRequest pageRequest) {
        if (active == null) {
            List<Product> products = productRepository.findAll(pageRequest).getContent();
            return productMapper.productsToProductDtos(products);
        } else {
            return productMapper.productsToProductDtos(productRepository.findAllByActive(active, pageRequest));
        }
    }

    private Product applyPatch(JsonPatch patch, Product product) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(product, JsonNode.class));
        return objectMapper.treeToValue(patched, Product.class);
    }
}
