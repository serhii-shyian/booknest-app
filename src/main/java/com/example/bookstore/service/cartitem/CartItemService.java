package com.example.bookstore.service.cartitem;

import com.example.bookstore.model.CartItem;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;

public interface CartItemService {
    CartItem saveCartItem(CartItem cartItem);

    CartItem findCartItemById(Long cartItemId);

    Set<CartItem> findAllCartItemsWithDependencies();

    List<CartItem> findCartItemsListById(Long shoppingCartId, Pageable pageable);

    void deleteCartItem(CartItem cartItem);
}
