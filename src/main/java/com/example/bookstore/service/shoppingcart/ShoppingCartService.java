package com.example.bookstore.service.shoppingcart;

import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.model.User;
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
