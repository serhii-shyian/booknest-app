package com.example.bookstore.repository;

import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
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
        ShoppingCart expected = shoppingCartRepository.findById(2L)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a shopping cart by id " + 2L));

        //When
        ShoppingCart actual = shoppingCartRepository.findByUserId(2L)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a shopping cart by userId " + 2L));

        //Then
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }
}
