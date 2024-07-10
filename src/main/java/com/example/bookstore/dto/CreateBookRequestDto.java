package com.example.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

public record CreateBookRequestDto(
        @NotEmpty(message = "Title may not be empty")
        String title,
        @NotEmpty(message = "Author may not be empty")
        String author,
        @NotEmpty(message = "Isbn may not be empty")
        @ISBN
        String isbn,
        @NotNull(message = "Price may not be null")
        @Positive
        BigDecimal price,
        @NotBlank(message = "Description may not be blank")
        @Length(min = 10, max = 300)
        String description,
        @NotBlank(message = "Cover image URL may not be blank")
        @URL
        String coverImage) {
}
