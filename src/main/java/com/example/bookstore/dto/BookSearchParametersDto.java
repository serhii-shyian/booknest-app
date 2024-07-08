package com.example.bookstore.dto;

public record BookSearchParametersDto(
        String title,
        String author,
        String isbn) {
}
