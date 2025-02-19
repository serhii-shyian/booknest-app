package com.example.booknest.service.shoppingcart;

import com.example.booknest.dto.cartitem.CartItemDto;
import com.example.booknest.dto.cartitem.CreateCartItemRequestDto;
import com.example.booknest.dto.cartitem.UpdateCartItemRequestDto;
import com.example.booknest.dto.shoppingcart.ShoppingCartDto;
import com.example.booknest.model.User;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartService {
    void createShoppingCart(User user);

    ShoppingCartDto findShoppingCart(
            User user,
            Pageable pageable);

    CartItemDto addBookToShoppingCart(
            User user,
            CreateCartItemRequestDto createCartDto);

    CartItemDto updateBookInShoppingCart(
            User user,
            Long cartItemId,
            UpdateCartItemRequestDto updateCartDto);

    void deleteBookFromShoppingCart(
            User user,
            Long cartItemId);
}
