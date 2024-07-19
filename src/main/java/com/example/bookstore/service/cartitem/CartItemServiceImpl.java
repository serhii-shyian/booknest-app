package com.example.bookstore.service.cartitem;

import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.repository.cartitem.CartItemRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;

    @Override
    public CartItem saveCartItem(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Override
    public CartItem findCartItemById(Long cartItemId) {
        return cartItemRepository.findByIdWithDependencies(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find CartItem by cartItemId: " + cartItemId));
    }

    @Override
    public List<CartItem> findCartItemsListById(Long shoppingCartId, Pageable pageable) {
        return cartItemRepository.findListByShoppingCartId(shoppingCartId, pageable);
    }

    @Override
    public Set<CartItem> findAllCartItemsWithDependencies() {
        return cartItemRepository.findAllCartItemsWithDependencies();
    }

    @Override
    public void deleteCartItem(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }
}
