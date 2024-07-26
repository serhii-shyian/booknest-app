package com.example.bookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Set;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

public record CreateBookRequestDto(
        @NotBlank(message = "Title may not be blank")
        String title,
        @NotBlank(message = "Author may not be blank")
        String author,
        @NotBlank(message = "Isbn may not be blank")
        @ISBN
        String isbn,
        @NotNull(message = "Price may not be null")
        @Positive
        BigDecimal price,
        @NotBlank(message = "Description may not be blank")
        @Length(min = 10, max = 255)
        String description,
        @NotBlank(message = "Cover image URL may not be blank")
        @URL
        String coverImage,
        @NotEmpty(message = "Category id's may not be blank")
        Set<Long> categoryIds) {
}
