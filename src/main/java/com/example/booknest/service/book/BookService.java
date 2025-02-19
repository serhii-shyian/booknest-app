package com.example.booknest.service.book;

import com.example.booknest.dto.book.BookDto;
import com.example.booknest.dto.book.BookDtoWithoutCategoryIds;
import com.example.booknest.dto.book.BookSearchParametersDto;
import com.example.booknest.dto.book.CreateBookRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto bookDto);

    BookDto findById(Long bookId);

    List<BookDto> findAll(Pageable pageable);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long categoryId, Pageable pageable);

    BookDto updateById(Long bookId, CreateBookRequestDto bookDto);

    void deleteById(Long bookId);

    List<BookDto> searchByParameters(BookSearchParametersDto paramsDto, Pageable pageable);
}
