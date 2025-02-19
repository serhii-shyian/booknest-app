package com.example.booknest.dto.user;

import com.example.booknest.validation.FieldMatch;
import com.example.booknest.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@FieldMatch(
        field = "password",
        fieldMatch = "repeatPassword",
        message = "Passwords values don't match!")
public record UserRegistrationRequestDto(
        @NotBlank(message = "Email may not be blank")
        @Email
        String email,
        @NotBlank(message = "Password may not be blank")
        @Password
        String password,
        @NotBlank(message = "Repeated password may not be blank")
        @Password
        String repeatPassword,
        @NotBlank(message = "First name may not be blank")
        @Length(min = 1, max = 50)
        String firstName,
        @NotBlank(message = "Last name may not be blank")
        @Length(min = 1, max = 50)
        String lastName,
        String shippingAddress) {
}
