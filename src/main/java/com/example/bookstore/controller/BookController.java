package com.example.bookstore.controller;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.book.BookSearchParametersDto;
import com.example.bookstore.dto.book.CreateBookRequestDto;
import com.example.bookstore.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
@Tag(name = "Book management", description = "Endpoint for managing books")
@Validated
@EnableMethodSecurity
public class BookController {
    private final BookService bookService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all books",
            description = "Getting a list of all available books")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<BookDto> getAll(@ParameterObject
                                    @PageableDefault(
                                            size = 5,
                                            sort = "title",
                                            direction = Sort.Direction.ASC)
                                    Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a book by id",
            description = "Getting a book by id if it's available")
    @PreAuthorize("hasRole('ROLE_USER')")
    public BookDto getBookById(@PathVariable @Positive Long id) {
        return bookService.findById(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all books by parameters",
            description = "Getting a list of all books according to the parameters")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public List<BookDto> searchBooks(BookSearchParametersDto searchParameters,
                                     @ParameterObject
                                     @PageableDefault(
                                             size = 5,
                                             sort = "title",
                                             direction = Sort.Direction.ASC)
                                     Pageable pageable) {
        return bookService.searchByParameters(searchParameters, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new book",
            description = "Creating a new book according to the parameters")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto bookRequestDto) {
        return bookService.save(bookRequestDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update a book by id",
            description = "Updating a book by id according to the parameters")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookDto updateBookById(@PathVariable @Positive Long id,
                                  @RequestBody @Valid CreateBookRequestDto bookRequestDto) {
        return bookService.updateById(id,bookRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a book by id",
            description = "Deleting a book by id if it's available")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteBookById(@PathVariable @Positive Long id) {
        bookService.deleteById(id);
    }
}
