package com.example.booknest.repository.book.spec;

import com.example.booknest.model.Book;
import com.example.booknest.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "author";

    @Override
    public String getKey() {
        return KEY;
    }

    public Specification<Book> getSpecification(String param) {
        return (root, query, criteriaBuilder)
                -> root.get(KEY).in(param);
    }
}
