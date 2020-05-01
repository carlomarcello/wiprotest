package com.cmarcello.wiprotest.web.mapper;

import com.cmarcello.wiprotest.domain.Product;
import com.cmarcello.wiprotest.web.model.ProductDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {DateMapper.class})
public interface ProductMapper {
    ProductDto productToProductDto(Product product);
    List<ProductDto> productsToProductDtos(List<Product> products);
    Product productDtoToProduct(ProductDto dto);
}
