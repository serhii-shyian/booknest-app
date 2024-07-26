package com.example.bookstore.repository.order;

import com.example.bookstore.model.Category;
import com.example.bookstore.model.Order;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Category> {
    List<Order> findAllByUserId(Long userId, Pageable pageable);
}
