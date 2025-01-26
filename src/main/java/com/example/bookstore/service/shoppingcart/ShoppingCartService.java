package com.example.bookstore.service.shoppingcart;

import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface ShoppingCartService {
    ShoppingCart createShoppingCart(User user);

    ShoppingCartDto findShoppingCart(
            Authentication authentication,
            Pageable pageable);

    CartItemDto addBookToShoppingCart(
            Authentication authentication,
            CreateCartItemRequestDto createCartDto);

    CartItemDto updateBookInShoppingCart(
            Authentication authentication,
            Long cartItemId,
            UpdateCartItemRequestDto updateCartDto);

    void deleteBookFromShoppingCart(
            Authentication authentication,
            Long cartItemId);
}
