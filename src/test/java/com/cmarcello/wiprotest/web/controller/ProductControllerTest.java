package com.cmarcello.wiprotest.web.controller;

import com.cmarcello.wiprotest.services.ProductService;
import com.cmarcello.wiprotest.web.controller.exception.ResourceNotFoundException;
import com.cmarcello.wiprotest.web.model.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService productService;

    ProductDto productDto;
    List<ProductDto> productDtoList;

    String validJsonSave;
    String invalidJsonSave;

    ArgumentCaptor<UUID> uuidArgument = ArgumentCaptor.forClass(UUID.class);
    ArgumentCaptor<ProductDto> productDtoArgument = ArgumentCaptor.forClass(ProductDto.class);
    ArgumentCaptor<Boolean> booleanArgument = ArgumentCaptor.forClass(Boolean.class);
    ArgumentCaptor<PageRequest> pageRequestArgument = ArgumentCaptor.forClass(PageRequest.class);

    @BeforeEach
    void setup() {
        productDto = ProductDto.builder()
                .id(UUID.randomUUID())
                .description("PRD1")
                .value(new BigDecimal(99.90))
                .active(true)
                .creationDate(OffsetDateTime.now())
                .build();

        validJsonSave = "{\"description\":\"PRD\",\"value\":\"99.99\",\"creationDate\":\"2020-05-01T14:35:04.230Z\",\"active\":true}";
        invalidJsonSave = "{\"description\":\"PRD\",\"value\":\"a\",\"creationDate\":\"2020-05-01T14:35:04.230Z\",\"active\":true}";

        productDtoList = new ArrayList<ProductDto>();
        productDtoList.add(ProductDto.builder().id(UUID.randomUUID()).description("PRD1").value(new BigDecimal(99.90)).active(true).creationDate(OffsetDateTime.now()).build());
        productDtoList.add(ProductDto.builder().id(UUID.randomUUID()).description("PRD2").value(new BigDecimal(99.90)).active(true).creationDate(OffsetDateTime.now()).build());
    }

    @Test
    void testGetByIdSuccess() throws Exception {
        when(productService.getById(productDto.getId())).thenReturn(productDto);

        mvc.perform(get("/api/v1/products/{productId}", productDto.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(productDto.getDescription())))
                .andExpect(jsonPath("$.value", is(productDto.getValue().toString())))
                .andExpect(jsonPath("$.active", is(productDto.getActive())));

        verify(productService, times(1)).getById(uuidArgument.capture());
    }

    @Test
    void testGetByIdNotFound() throws Exception {
        when(productService.getById(any())).thenThrow(new ResourceNotFoundException());

        mvc.perform(get("/api/v1/products/{productId}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getById(uuidArgument.capture());
    }

    @Test
    void testGetByIdInvalidParameter() throws Exception {
        mvc.perform(get("/api/v1/products/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).getById(uuidArgument.capture());
    }

    @Test
    void testSaveSuccess() throws Exception {
        when(productService.save(any(ProductDto.class))).thenReturn(productDto);

        mvc.perform(post("/api/v1/products")
                .content(validJsonSave)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(productDto.getId().toString())));

        verify(productService, times(1)).save(productDtoArgument.capture());
    }

    @Test
    void testSaveInvalidBody() throws Exception {
        mvc.perform(post("/api/v1/products")
                .content(invalidJsonSave)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).save(productDtoArgument.capture());
    }

    @Test
    void testUpdateSuccess() throws Exception {
        when(productService.update(productDto.getId(), productDto)).thenReturn(productDto);

        mvc.perform(put("/api/v1/products/{productId}", productDto.getId())
                .content(validJsonSave)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).update(uuidArgument.capture(), productDtoArgument.capture());
    }

    @Test
    void testUpdateInvalidBody() throws Exception {
        mvc.perform(put("/api/v1/products/{productId}", productDto.getId())
                .content(invalidJsonSave)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).update(uuidArgument.capture(), productDtoArgument.capture());
    }

    @Test
    void testFindAllPageableWithoutFilterSuccess() throws Exception {
        when(productService.findAll(any(PageRequest.class))).thenReturn(productDtoList);

        HttpHeaders headers = new HttpHeaders();
        headers.set("pageNum", "0");
        headers.set("pageSize", "5");

        mvc.perform(get("/api/v1/products")
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(productDtoList.size())));

        verify(productService, times(1)).findAll(pageRequestArgument.capture());
        verify(productService, times(0)).findAll(booleanArgument.capture(), pageRequestArgument.capture());
    }

    @Test
    void testFindAllPageableByActiveSuccess() throws Exception {
        when(productService.findAll(anyBoolean(), any(PageRequest.class))).thenReturn(productDtoList);

        HttpHeaders headers = new HttpHeaders();
        headers.set("pageNum", "0");
        headers.set("pageSize", "5");

        mvc.perform(get("/api/v1/products")
                .headers(headers)
                .param("active", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(productDtoList.size())));

        verify(productService, times(0)).findAll(pageRequestArgument.capture());
        verify(productService, times(1)).findAll(booleanArgument.capture(), pageRequestArgument.capture());
    }

    @Test
    void testFindAllPageableInvalidHeader() throws Exception {
        mvc.perform(get("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).findAll(pageRequestArgument.capture());
        verify(productService, times(0)).findAll(booleanArgument.capture(), pageRequestArgument.capture());
    }

    @Test
    void testFindAllPageableByActiveInvalidActiveParam() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("pageNum", "0");
        headers.set("pageSize", "5");

        mvc.perform(get("/api/v1/products")
                .headers(headers)
                .param("active", "a")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(productService, times(0)).findAll(pageRequestArgument.capture());
        verify(productService, times(0)).findAll(booleanArgument.capture(), pageRequestArgument.capture());
    }
}