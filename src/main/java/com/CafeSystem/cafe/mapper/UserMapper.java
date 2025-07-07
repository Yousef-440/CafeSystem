package com.CafeSystem.cafe.mapper;
import com.CafeSystem.cafe.dto.UserDto;
import com.CafeSystem.cafe.dto.productDto.ProductDto;
import com.CafeSystem.cafe.model.Product;
import com.CafeSystem.cafe.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto userDto);
    UserDto toDto(User user);

    Product toEntity(ProductDto productDto);
    ProductDto toDot(Product product);
}