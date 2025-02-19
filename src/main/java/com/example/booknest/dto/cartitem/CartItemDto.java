package com.example.booknest.dto.cartitem;

public record CartItemDto(
        Long id,
        Long bookId,
        String bookTitle,
        Integer quantity) {
}
