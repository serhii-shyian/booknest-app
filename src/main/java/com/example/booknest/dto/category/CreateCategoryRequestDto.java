package com.example.booknest.dto.category;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateCategoryRequestDto(
        @NotBlank(message = "Name may not be blank")
        String name,
        @NotBlank(message = "Description may not be blank")
        @Length(min = 10, max = 255)
        String description) {
}
