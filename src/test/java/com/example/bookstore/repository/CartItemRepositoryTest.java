package com.example.bookstore.repository;

import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.repository.cartitem.CartItemRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartItemRepositoryTest {
    @Autowired
    private CartItemRepository cartItemRepository;

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
            Find cart items list by shopping cart id when shopping cart id exists
            """)
    @Sql(
            scripts = "classpath:database/cartitems/insert-data-ci_repository_test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/cartitems/delete-data-ci_repository_test.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void findCartItemsListByShoppingCartId_ExistedShoppingCartId_ReturnsCartItemsList() {
        //Given
        List<CartItem> expected = List.of(cartItemRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find cart item by id " + 1L)));

        //When
        List<CartItem> actual = cartItemRepository
                .findListByShoppingCartId(2L, Pageable.ofSize(5));

        //Then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(actual, expected);
    }
}
