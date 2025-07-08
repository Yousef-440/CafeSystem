package com.CafeSystem.cafe.mapper;
import com.CafeSystem.cafe.dto.UserDto;
import com.CafeSystem.cafe.dto.productDto.GetAllProductResponse;
import com.CafeSystem.cafe.dto.productDto.ProductDto;
import com.CafeSystem.cafe.model.Product;
import com.CafeSystem.cafe.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto userDto);
    UserDto toDto(User user);

    Product toEntity(ProductDto productDto);
    ProductDto toDot(Product product);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    GetAllProductResponse toDto(Product product);
}