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
import com.example.bookstore.repository.user.UserRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserRepository userRepository;

    @Override
    public ShoppingCart createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        return shoppingCartRepository.save(shoppingCart);
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
        Book bookFromDB = findBookByBookId(createCartDto);
        ShoppingCart shoppingCartFromDB = findShoppingCartByUserId(authentication);
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
            Authentication authentication,
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
            Authentication authentication, Long cartItemId) {
        CartItem cartItemFromDB = findCartItemById(cartItemId);
        isShoppingCartContainsCartItem(cartItemId);
        cartItemRepository.delete(cartItemFromDB);
    }

    private ShoppingCart findShoppingCartByUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "User not found with username: " + username));
            return shoppingCartRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find ShoppingCart by userId: " + user.getId()));
        } else {
            throw new EntityNotFoundException(
                    "Authenticated principal is not a UserDetails instance");
        }
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
            Authentication authentication, Pageable pageable) {
        Long shoppingCartId = findShoppingCartByUserId(authentication).getId();
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
