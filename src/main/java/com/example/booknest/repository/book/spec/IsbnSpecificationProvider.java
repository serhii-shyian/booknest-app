package com.example.booknest.repository.book.spec;

import com.example.booknest.model.Book;
import com.example.booknest.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "isbn";

    @Override
    public String getKey() {
        return KEY;
    }

    public Specification<Book> getSpecification(String param) {
        return (root, query, criteriaBuilder)
                -> root.get(KEY).in(param);
    }
}
