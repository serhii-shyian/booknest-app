package com.example.booknest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.booknest.dto.cartitem.CartItemDto;
import com.example.booknest.dto.cartitem.CreateCartItemRequestDto;
import com.example.booknest.dto.cartitem.UpdateCartItemRequestDto;
import com.example.booknest.dto.shoppingcart.ShoppingCartDto;
import com.example.booknest.exception.EntityNotFoundException;
import com.example.booknest.mapper.CartItemMapper;
import com.example.booknest.mapper.ShoppingCartMapper;
import com.example.booknest.model.Book;
import com.example.booknest.model.CartItem;
import com.example.booknest.model.ShoppingCart;
import com.example.booknest.model.User;
import com.example.booknest.repository.book.BookRepository;
import com.example.booknest.repository.cartitem.CartItemRepository;
import com.example.booknest.repository.shoppingcart.ShoppingCartRepository;
import com.example.booknest.service.shoppingcart.ShoppingCartServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private BookRepository bookRepository;

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

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);
        when(cartItemRepository.findListByShoppingCartId(expected.getId(), pageable))
                .thenReturn(List.of(cartItem));
        when(cartItemMapper.toDto(cartItem)).thenReturn(cartItemDto);

        //When
        ShoppingCartDto actual = shoppingCartService.findShoppingCart(user, pageable);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(2)).findByUserId(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verify(cartItemRepository, times(1)).findListByShoppingCartId(expected.getId(), pageable);
        verify(cartItemMapper, times(1)).toDto(cartItem);
    }

    @Test
    @DisplayName("""
            Find ShoppingCart when given non existing user
            """)
    void findShoppingCart_NonExistingUser_ThrowsException() {
        //Given
        User user = getTestUser();

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.empty());

        //Then
        Pageable pageable = PageRequest.of(0, 5);
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.findShoppingCart(user, pageable));
        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
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

        when(shoppingCartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findById(requestDto.bookId()))
                .thenReturn(Optional.of(book));
        when(cartItemMapper.toEntity(requestDto)).thenReturn(cartItem);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        //When
        CartItemDto actual = shoppingCartService
                .addBookToShoppingCart(user, requestDto);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(shoppingCartRepository, times(1)).findByUserId(user.getId());
        verify(bookRepository, times(1)).findById(requestDto.bookId());
        verify(cartItemMapper, times(1)).toEntity(requestDto);
        verify(cartItemMapper, times(1)).toDto(cartItem);
    }

    @Test
    @DisplayName("""
            Add book to ShoppingCart when book does not exist
            """)
    void addBookToShoppingCart_NonExistingBook_ThrowsException() {
        //Given
        User user = new User();
        Book book = getBook();
        CreateCartItemRequestDto requestDto = getCartItemRequestDto(book.setId(99L));

        when(bookRepository.findById(requestDto.bookId()))
                .thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> shoppingCartService
                .addBookToShoppingCart(user, requestDto));
        verify(bookRepository, times(1)).findById(requestDto.bookId());
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

        when(cartItemRepository.findById(cartItem.getId()))
                .thenReturn(Optional.of(cartItem));
        when(cartItemRepository.findAll()).thenReturn(List.of(cartItem));
        when(cartItemMapper.toDto(cartItem)).thenReturn(expected);

        //When
        CartItemDto actual = shoppingCartService
                .updateBookInShoppingCart(user, cartItem.getId(), updateRequestDto);

        //Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(cartItemRepository, times(2)).findById(cartItem.getId());
        verify(cartItemRepository, times(1)).findAll();
        verify(cartItemMapper, times(1)).toDto(cartItem);
    }

    @Test
    @DisplayName("""
            Update book in ShoppingCart when cart item does not exist
            """)
    void updateBookInShoppingCart_NonExistingCartItem_ThrowsException() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Book book = getBook();
        CartItem cartItem = getCartItem(shoppingCart, book);
        UpdateCartItemRequestDto updateRequestDto = new UpdateCartItemRequestDto(10);

        when(cartItemRepository.findById(cartItem.getId()))
                .thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> shoppingCartService
                .updateBookInShoppingCart(user, cartItem.getId(), updateRequestDto));
        verify(cartItemRepository, times(1)).findById(cartItem.getId());
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

        when(cartItemRepository.findById(cartItem.getId()))
                .thenReturn(Optional.of(cartItem));
        when(cartItemRepository.findAll()).thenReturn(List.of(cartItem));

        //When
        shoppingCartService.deleteBookFromShoppingCart(user, cartItem.getId());

        //Then
        verify(cartItemRepository, times(2)).findById(cartItem.getId());
        verify(cartItemRepository, times(1)).findAll();
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    @DisplayName("""
            Delete book from ShoppingCart when CartItem Id does not exist
            """)
    void deleteBookFromShoppingCart_NonExistingId_ThrowsException() {
        //Given
        User user = getTestUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        Book book = getBook();
        CartItem cartItem = getCartItem(shoppingCart, book);

        when(cartItemRepository.findById(cartItem.getId()))
                .thenReturn(Optional.empty());

        //Then
        assertThrows(EntityNotFoundException.class, () -> {
            shoppingCartService.deleteBookFromShoppingCart(user, cartItem.getId());
        });
        verify(cartItemRepository, times(1)).findById(cartItem.getId());
    }

    private User getTestUser() {
        return new User()
                .setId(2L)
                .setEmail("user@i.ua")
                .setPassword("qwerty123")
                .setFirstName("John")
                .setLastName("Smith")
                .setShippingAddress("Ukraine");

    }

    private ShoppingCart getShoppingCart(User user) {
        return new ShoppingCart()
                .setId(user.getId())
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
