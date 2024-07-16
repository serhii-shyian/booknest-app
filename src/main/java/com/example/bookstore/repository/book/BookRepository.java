package com.example.bookstore.repository.book;

import com.example.bookstore.model.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long>,
        JpaSpecificationExecutor<Book> {
    @Query("from Book b left join fetch b.categories")
    List<Book> findAllWithCategories(Pageable pageable);

    @Query("from Book b left join fetch b.categories c where c.id = :categoryId")
    List<Book> findAllByCategoryId(Long categoryId, Pageable pageable);

    @Query("from Book b left join fetch b.categories c where b.id = :id")
    Optional<Book> findByIdWithCategory(Long id);
}
