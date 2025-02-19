package com.example.booknest.service.user;

import com.example.booknest.dto.user.UserRegistrationRequestDto;
import com.example.booknest.dto.user.UserResponseDto;
import com.example.booknest.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto registrationDto)
            throws RegistrationException;
}
