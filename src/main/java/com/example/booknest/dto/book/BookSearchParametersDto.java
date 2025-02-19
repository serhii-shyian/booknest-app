package com.example.booknest.dto.book;

public record BookSearchParametersDto(
        String title,
        String author,
        String isbn) {
}
