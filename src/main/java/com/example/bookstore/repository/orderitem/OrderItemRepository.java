package com.example.bookstore.repository.orderitem;

import com.example.bookstore.model.Category;
import com.example.bookstore.model.OrderItem;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>,
        JpaSpecificationExecutor<Category> {
    List<OrderItem> findAllByOrderId(Long orderId, Pageable pageable);
}
