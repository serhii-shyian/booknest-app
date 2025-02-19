package com.example.booknest.dto.orderitem;

public record OrderItemDto(
        Long id,
        Long bookId,
        Integer quantity) {
}
