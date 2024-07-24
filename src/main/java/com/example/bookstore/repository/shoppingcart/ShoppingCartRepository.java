package com.example.bookstore.repository.shoppingcart;

import com.example.bookstore.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long>,
        JpaSpecificationExecutor<ShoppingCart> {
    @Query("from ShoppingCart sc "
            + "left join fetch sc.user u "
            + "left join fetch sc.cartItems "
            + "where u.id = :userId")
    Optional<ShoppingCart> findByUserId(Long userId);
}
