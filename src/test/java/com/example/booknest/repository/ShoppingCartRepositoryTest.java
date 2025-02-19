package com.example.booknest.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.booknest.exception.EntityNotFoundException;
import com.example.booknest.model.Book;
import com.example.booknest.model.CartItem;
import com.example.booknest.model.ShoppingCart;
import com.example.booknest.model.User;
import com.example.booknest.repository.shoppingcart.ShoppingCartRepository;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/delete-all-data-before-tests.sql"));
        }
    }

    @Test
    @DisplayName("""
            Find shopping cart by user id when user exists
            """)
    @Sql(
            scripts = "classpath:database/cartitems/insert-data-ci_repository_test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/cartitems/delete-data-ci_repository_test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findByUserId_GivenValidUserId_ShouldReturnShoppingCart() {
        //Given
        Book book = getBook();
        User user = getUser();
        ShoppingCart shoppingCart = getShoppingCart(user);
        CartItem cartItem = getCartItem(shoppingCart, book);
        ShoppingCart expected = shoppingCart.setCartItems(Set.of(cartItem));

        //When
        ShoppingCart actual = shoppingCartRepository.findByUserId(2L)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a shopping cart by userId " + 2L));

        //Then
        assertNotNull(actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("user", "cartItems")
                .isEqualTo(expected);
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

    private User getUser() {
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

    private CartItem getCartItem(ShoppingCart shoppingCart, Book book) {
        return new CartItem()
                .setId(1L)
                .setShoppingCart(shoppingCart)
                .setBook(book)
                .setQuantity(6);
    }
}
