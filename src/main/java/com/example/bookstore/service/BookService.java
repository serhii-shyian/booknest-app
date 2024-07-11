package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookSearchParametersDto;
import com.example.bookstore.dto.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    BookDto findById(Long id);

    List<BookDto> findAll(Pageable pageable);

    BookDto updateById(Long id, CreateBookRequestDto bookRequestDto);

    void deleteById(Long id);

    List<BookDto> searchByParameters(BookSearchParametersDto params);
}
