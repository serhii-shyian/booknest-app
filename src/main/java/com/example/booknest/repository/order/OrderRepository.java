package com.example.booknest.repository.order;

import com.example.booknest.model.Category;
import com.example.booknest.model.Order;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Category> {
    List<Order> findAllByUserId(Long userId, Pageable pageable);
}
