package com.example.bookstore.service.shoppingcart;

import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.cartitem.CartItemRepository;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final BookRepository bookRepository;

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartDto findShoppingCart(
            User user, Pageable pageable) {
        ShoppingCart shoppingCartFromDB = findShoppingCartByUserId(user.getId());
        ShoppingCartDto shoppingCartDto = shoppingCartMapper.toDto(shoppingCartFromDB);
        Set<CartItemDto> cartItemsSetById = findSetByShoppingCartId(user, pageable);
        shoppingCartDto.setCartItems(cartItemsSetById);
        return shoppingCartDto;
    }

    @Override
    public CartItemDto addBookToShoppingCart(
            User user, CreateCartItemRequestDto createCartDto) {
        Book bookFromDB = findBookByBookId(createCartDto);
        ShoppingCart shoppingCartFromDB = findShoppingCartByUserId(user.getId());
        CartItem cartItemFromDB = shoppingCartFromDB.getCartItems().stream().filter(
                        cartItem -> cartItem.getBook().getId().equals(bookFromDB.getId()))
                .findFirst().orElseGet(() -> {
                    CartItem cartItem = cartItemMapper.toEntity(createCartDto);
                    cartItem.setShoppingCart(shoppingCartFromDB);
                    cartItem.setBook(bookFromDB);
                    return cartItem;
                });
        cartItemFromDB.setQuantity(createCartDto.quantity());
        cartItemRepository.save(cartItemFromDB);
        return cartItemMapper.toDto(cartItemFromDB);
    }

    @Override
    public CartItemDto updateBookInShoppingCart(
            User user,
            Long cartItemId,
            UpdateCartItemRequestDto updateCartDto) {
        CartItem cartItemFromDB = findCartItemById(cartItemId);
        isShoppingCartContainsCartItem(cartItemId);
        cartItemFromDB.setQuantity(updateCartDto.quantity());
        cartItemRepository.save(cartItemFromDB);
        return cartItemMapper.toDto(cartItemFromDB);
    }

    @Override
    public void deleteBookFromShoppingCart(
            User user, Long cartItemId) {
        CartItem cartItemFromDB = findCartItemById(cartItemId);
        isShoppingCartContainsCartItem(cartItemId);
        cartItemRepository.delete(cartItemFromDB);
    }

    private ShoppingCart findShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find ShoppingCart by userId: " + userId));
    }

    private Book findBookByBookId(CreateCartItemRequestDto createCartDto) {
        return bookRepository.findById(createCartDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find Book by bookId: " + createCartDto.bookId()));
    }

    private CartItem findCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find CartItem by cartItemId: " + cartItemId));
    }

    private List<CartItem> findCartItemsListById(Long shoppingCartId, Pageable pageable) {
        return cartItemRepository.findListByShoppingCartId(shoppingCartId, pageable);
    }

    public Set<CartItemDto> findSetByShoppingCartId(
            User user, Pageable pageable) {
        Long shoppingCartId = findShoppingCartByUserId(user.getId()).getId();
        return findCartItemsListById(shoppingCartId, pageable).stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void isShoppingCartContainsCartItem(Long cartItemId) {
        List<CartItem> cartItemsListById = cartItemRepository.findAll();
        CartItem cartItemById = findCartItemById(cartItemId);
        if (cartItemsListById.stream().noneMatch(
                cartItem -> cartItem.getId().equals(cartItemById.getId()))) {
            throw new EntityNotFoundException(
                    "Can't find CartItem in the ShoppingCart by id: " + cartItemId);
        }
    }
}
