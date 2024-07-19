package com.example.bookstore.service.shoppingcart;

import com.example.bookstore.dto.book.BookDto;
import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.BookMapper;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookstore.service.book.BookService;
import com.example.bookstore.service.cartitem.CartItemService;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemService cartItemService;
    private final CartItemMapper cartItemMapper;
    private final BookService bookService;
    private final BookMapper bookMapper;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto findShoppingCart(
            Authentication authentication, Pageable pageable) {
        ShoppingCart shoppingCartFromDB = findShoppingCartByUserId(authentication);
        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(shoppingCartFromDB);
        Set<CartItemDto> cartItemsSetById = findSetByShoppingCartId(authentication, pageable);
        shoppingCartDto.setCartItems(cartItemsSetById);
        return shoppingCartDto;
    }

    @Override
    public CartItemDto addBookToShoppingCart(
            Authentication authentication, CreateCartItemRequestDto createCartDto) {
        ShoppingCart shoppingCartFromDB = findShoppingCartByUserId(authentication);
        CartItem cartItem = cartItemMapper.toEntity(createCartDto);
        cartItem.setShoppingCart(shoppingCartFromDB);
        BookDto bookDto = bookService.findById(createCartDto.bookId());
        cartItem.setBook(bookMapper.toEntityFromBookDto(bookDto));
        return cartItemMapper.toDto(cartItemService.saveCartItem(cartItem));
    }

    @Override
    public CartItemDto updateBookInShoppingCart(
            Authentication authentication,
            Long cartItemId,
            UpdateCartItemRequestDto updateCartDto) {
        CartItem cartItem = cartItemService.findCartItemById(cartItemId);
        isShoppingCartContainsCartItem(cartItemId);
        cartItem.setQuantity(updateCartDto.quantity());
        cartItemService.saveCartItem(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public void deleteBookFromShoppingCart(
            Authentication authentication, Long cartItemId) {
        CartItem cartItem = cartItemService.findCartItemById(cartItemId);
        isShoppingCartContainsCartItem(cartItemId);
        cartItemService.deleteCartItem(cartItem);
    }

    private ShoppingCart findShoppingCartByUserId(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find ShoppingCart by userId: " + user.getId()));
    }

    public Set<CartItemDto> findSetByShoppingCartId(
            Authentication authentication, Pageable pageable) {
        Long shoppingCartId = findShoppingCartByUserId(authentication).getId();
        return cartItemService.findCartItemsListById(shoppingCartId, pageable).stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void isShoppingCartContainsCartItem(Long cartItemId) {
        Set<CartItem> cartItemsSetById = cartItemService.findAllCartItemsWithDependencies();
        CartItem cartItemById = cartItemService.findCartItemById(cartItemId);
        if (cartItemsSetById.stream().noneMatch(
                cartItem -> cartItem.getId().equals(cartItemById.getId()))) {
            throw new EntityNotFoundException(
                    "Can't find CartItem in the ShoppingCart by id: " + cartItemId);
        }
    }
}
