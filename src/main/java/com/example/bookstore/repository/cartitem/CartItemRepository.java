package com.example.bookstore.repository.cartitem;

import com.example.bookstore.model.CartItem;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long>,
        JpaSpecificationExecutor<CartItem> {
    List<CartItem> findListByShoppingCartId(Long shoppingCartId, Pageable pageable);

    @Query("from CartItem ci "
            + "left join fetch ci.shoppingCart "
            + "left join fetch ci.book ")
    Set<CartItem> findAllCartItemsSet();
}
