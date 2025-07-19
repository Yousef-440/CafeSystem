package com.CafeSystem.cafe.mapper;
import com.CafeSystem.cafe.dto.SignUpUserDto;
import com.CafeSystem.cafe.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(SignUpUserDto signUpUserDto);
}