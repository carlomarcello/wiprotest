package com.cmarcello.wiprotest.web.controller;

import com.cmarcello.wiprotest.services.ProductService;
import com.cmarcello.wiprotest.web.model.ProductDto;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/v1/products")
@RestController
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity getById(@PathVariable("productId") UUID productId) {
        return new ResponseEntity<>(productService.getById(productId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity save(@RequestBody @Validated ProductDto productDto) {
        return new ResponseEntity<>(productService.save(productDto), HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity update(@PathVariable("productId") UUID productId, @RequestBody @Validated ProductDto productDto) {
        return new ResponseEntity<>(productService.update(productId, productDto), HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> listAll(
            @RequestHeader("pageNum") Integer pageNum,
            @RequestHeader("pageSize") Integer pageSize,
            @RequestParam(required = false) Boolean active) {

        final PageRequest pageRequest = PageRequest.of(pageNum, pageSize);

        if (active == null) {
            return new ResponseEntity<>(productService.findAll(pageRequest), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(productService.findAll(active, pageRequest), HttpStatus.OK);
        }

    }

    @PatchMapping("/{productId}")
    public ResponseEntity update(@PathVariable("productId") UUID productId, @RequestBody JsonPatch patch) {
        return new ResponseEntity<>(productService.update(productId, patch), HttpStatus.NO_CONTENT);
    }
}
