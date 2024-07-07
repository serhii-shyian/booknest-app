package com.example.bookstore.dto;

import java.math.BigDecimal;

public record BookDto(
        String title,
        String author,
        String isbn,
        BigDecimal price,
        String description,
        String coverImage) {
}
