package com.example.booknest.mapper;

import com.example.booknest.config.MapperConfig;
import com.example.booknest.dto.user.UserRegistrationRequestDto;
import com.example.booknest.dto.user.UserResponseDto;
import com.example.booknest.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toEntity(UserRegistrationRequestDto registrationDto);
}
