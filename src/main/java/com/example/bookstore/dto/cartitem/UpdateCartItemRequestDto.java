package com.example.bookstore.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCartItemRequestDto(
        @NotNull(message = "Book quantity may not be null")
        @Positive
        Integer quantity) {
}
