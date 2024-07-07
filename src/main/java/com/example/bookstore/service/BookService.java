package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    BookDto findById(Long id);

    List<BookDto> findAll();

    BookDto updateById(Long id, CreateBookRequestDto bookRequestDto);

    void deleteById(Long id);
}
