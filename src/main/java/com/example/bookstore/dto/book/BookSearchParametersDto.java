package com.example.bookstore.dto.book;

public record BookSearchParametersDto(
        String title,
        String author,
        String isbn) {
}
