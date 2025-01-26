package com.example.bookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.mapper.CartItemMapper;
import com.example.bookstore.mapper.ShoppingCartMapper;
import com.example.bookstore.model.Book;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.Role;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.book.BookRepository;
import com.example.bookstore.repository.cartitem.CartItemRepository;
import com.example.bookstore.repository.role.RoleRepository;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookstore.repository.user.UserRepository;
import com.example.bookstore.service.shoppingcart.ShoppingCartServiceImpl;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartItemMapper cartItemMapper;
    @Mock
    private Authentication authentication;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;

    @Test
    @DisplayName("""
            Create ShoppingCart when given valid user
            """)
    public void createShoppingCart_ValidUser_CreatesShoppingCart() {
        //Given
        User user = getTestUser();
        ShoppingCart expected = getShoppingCart(user);
        Mockito.when(shoppingCartRepository.save(any())).thenReturn(expected);

        //When
        ShoppingCart actual = shoppingCartService.createShoppingCart(user);

        //Then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Find ShoppingCart when given an existing user
            """)
    void findShoppingCart_ExistingUser_ReturnsShoppingCartDto() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Book book = getBook();
        CartItem cartItem = getCartItem(shoppingCart, book);
        CartItemDto cartItemDto = getCartItemDto(cartItem);
        Pageable pageable = PageRequest.of(0, 5);
        ShoppingCartDto expected = new ShoppingCartDto()
                .setId(shoppingCart.getId())
                .setUserId(shoppingCart.getUser().getId())
                .setCartItems(Set.of(cartItemDto));

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        Mockito.when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        Mockito.when(cartItemRepository.findListByShoppingCartId(expected.getId(), pageable))
                .thenReturn(List.of(cartItem));
        Mockito.when(cartItemMapper.toDto(any())).thenReturn(cartItemDto);

        //When
        ShoppingCartDto actual = shoppingCartService.findShoppingCart(authentication, pageable);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Find ShoppingCart when given non existing user
            """)
    void findShoppingCart_NonExistingUser_ThrowsException() {
        //Given
        User user = getTestUser();

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.empty());

        //Then
        Pageable pageable = PageRequest.of(0, 5);
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.findShoppingCart(authentication, pageable));
    }

    @Test
    @DisplayName("""
            Add book to ShoppingCart when given valid book
            """)
    void addBookToShoppingCart_ValidBook_ReturnsCartItemDto() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Book book = getBook();
        CreateCartItemRequestDto requestDto = getCartItemRequestDto(book);
        CartItem cartItem = getCartItem(shoppingCart, book);
        CartItemDto expected = getCartItemDto(cartItem);

        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        Mockito.when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        Mockito. when(bookRepository.findById(requestDto.bookId()))
                .thenReturn(Optional.of(book));
        Mockito.when(cartItemMapper.toEntity(requestDto)).thenReturn(cartItem);
        Mockito.when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        //When
        CartItemDto actual = shoppingCartService
                .addBookToShoppingCart(authentication, requestDto);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Add book to ShoppingCart when book does not exist
            """)
    void addBookToShoppingCart_NonExistingBook_ThrowsException() {
        //Given
        Book book = getBook();
        CreateCartItemRequestDto requestDto = getCartItemRequestDto(book.setId(99L));

        Mockito.when(bookRepository.findById(requestDto.bookId()))
                .thenReturn(Optional.empty());

        //Then
        Assertions.assertThrows(EntityNotFoundException.class, () -> shoppingCartService
                .addBookToShoppingCart(authentication, requestDto));
    }

    @Test
    @DisplayName("""
            Update book in ShoppingCart when given valid input data
            """)
    void updateBookInShoppingCart_ValidData_ReturnsCartItemDto() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Book book = getBook();
        CartItem cartItem = getCartItem(shoppingCart, book);
        UpdateCartItemRequestDto updateRequestDto = new UpdateCartItemRequestDto(10);
        CartItemDto expected = new CartItemDto(
                cartItem.getId(),
                cartItem.getBook().getId(),
                cartItem.getBook().getTitle(),
                10);

        Mockito.when(cartItemRepository.findById(cartItem.getId()))
                .thenReturn(Optional.of(cartItem));
        Mockito.when(cartItemRepository.findAll()).thenReturn(List.of(cartItem));
        Mockito.when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        //When
        CartItemDto actual = shoppingCartService
                .updateBookInShoppingCart(authentication, cartItem.getId(), updateRequestDto);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Delete book from ShoppingCart when CartItem Id exist
            """)
    void deleteBookFromShoppingCart_ExistingId_ReturnsNothing() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Book book = getBook();
        CartItem cartItem = getCartItem(shoppingCart, book);

        Mockito.when(cartItemRepository.findById(cartItem.getId()))
                .thenReturn(Optional.of(cartItem));
        Mockito.when(cartItemRepository.findAll()).thenReturn(List.of(cartItem));

        //When
        shoppingCartService.deleteBookFromShoppingCart(authentication, cartItem.getId());

        //Then
        Mockito.verify(cartItemRepository).delete(cartItem);
    }

    private User getTestUser() {
        return new User()
                .setId(1L)
                .setEmail("user@i.ua")
                .setPassword("qwerty123")
                .setFirstName("John")
                .setLastName("Smith")
                .setShippingAddress("Ukraine")
                .setRoles(roleRepository.findAllByNameContaining(
                        Collections.singletonList(Role.RoleName.USER)));

    }

    private ShoppingCart getShoppingCart(User user) {
        return new ShoppingCart()
                .setId(1L)
                .setUser(user);
    }

    private Book getBook() {
        return new Book()
                .setId(1L)
                .setTitle("Sample Book 1")
                .setAuthor("Author A")
                .setIsbn("978-1-23-456789-7")
                .setPrice(BigDecimal.valueOf(19.99))
                .setDescription("This is a sample book description.")
                .setCoverImage("http://example.com/cover1.jpg");

    }

    private CartItem getCartItem(ShoppingCart shoppingCart, Book book) {
        return new CartItem()
                .setId(1L)
                .setShoppingCart(shoppingCart)
                .setBook(book)
                .setQuantity(1);
    }

    private CartItemDto getCartItemDto(CartItem cartItem) {
        return new CartItemDto(
                cartItem.getId(),
                cartItem.getBook().getId(),
                cartItem.getBook().getTitle(),
                cartItem.getQuantity());
    }

    private CreateCartItemRequestDto getCartItemRequestDto(Book book) {
        return new CreateCartItemRequestDto(
                book.getId(),
                1);
    }
}
