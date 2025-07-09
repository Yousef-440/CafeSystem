package com.CafeSystem.cafe.mapper;

import com.CafeSystem.cafe.dto.productDto.GetAllProductResponse;
import com.CafeSystem.cafe.dto.productDto.ProductDto;
import com.CafeSystem.cafe.dto.productDto.ProductUpdateResponse;
import com.CafeSystem.cafe.dto.productDto.UpdateStatusResponse;
import com.CafeSystem.cafe.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface ProductMapper {
    @Mapping(source = "name", target = "nameProduct")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    UpdateStatusResponse convertToProduct(Product product);

    @Mapping(source = "price", target = "price")
    ProductUpdateResponse convert(Product product);

    Product toEntity(ProductDto productDto);
    ProductDto toDot(Product product);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    GetAllProductResponse toDto(Product product);
}
