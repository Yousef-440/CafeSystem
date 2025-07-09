package com.CafeSystem.cafe.mapper;
import com.CafeSystem.cafe.dto.UserDto;
import com.CafeSystem.cafe.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserDto userDto);
    UserDto toDto(User user);





}