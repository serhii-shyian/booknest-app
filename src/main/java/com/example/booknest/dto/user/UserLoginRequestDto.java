package com.example.booknest.dto.user;

import com.example.booknest.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @NotBlank(message = "Email may not be blank")
        @Email
        String email,
        @NotBlank(message = "Password may not be blank")
        @Password
        String password) {
}
