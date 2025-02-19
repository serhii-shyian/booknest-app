package com.example.booknest.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCartItemRequestDto(
        @NotNull(message = "Book id may not be null")
        @Positive
        Long bookId,
        @NotNull(message = "Book quantity may not be null")
        @Positive
        Integer quantity) {
}
