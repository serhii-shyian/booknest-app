package com.example.bookstore.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bookstore.dto.cartitem.CartItemDto;
import com.example.bookstore.dto.cartitem.CreateCartItemRequestDto;
import com.example.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.example.bookstore.dto.shoppingcart.ShoppingCartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ShoppingCartControllerTest {
    private static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/delete-all-data-before-tests.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/books/insert-into-books.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/users/insert-into-users.sql"));
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/shoppingcarts/insert-into-shopping_carts.sql"));
        }
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/books/delete-all-from-books.sql")
            );
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/shoppingcarts/delete-all-from-shopping_carts.sql")
            );
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource(
                            "database/users/delete-all-from-users.sql")
            );
        }
    }

    @Test
    @Order(1)
    @DisplayName("""
            Get ShoppingCart by user id when user exists
            """)
    @WithMockUser(username = "user@i.ua")
    @Sql(
            scripts = "classpath:database/cartitems/insert-into-cart_items.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/cartitems/delete-from-cart_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getUserShoppingCart_ExistingUser_ReturnsShoppingCartDto() throws Exception {
        //Given
        ShoppingCartDto expected = getShoppingCartDto();

        //When
        MvcResult result = mockMvc.perform(
                        get("/cart")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        //Then
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse()
                        .getContentAsByteArray(), ShoppingCartDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(2)
    @DisplayName("""
            Add book to ShoppingCart when book exists
            """)
    @WithMockUser(username = "user@i.ua")
    @Sql(
            scripts = "classpath:database/cartitems/delete-from-cart_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void addBookToShoppingCart_ExistingBook_ReturnsCartItemDto() throws Exception {
        //Given
        CreateCartItemRequestDto requestDto = getCreateCartItemRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CartItemDto expected = getCartItemDto();

        //When
        MvcResult result = mockMvc.perform(
                        post("/cart")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        CartItemDto actual = objectMapper.readValue(
                result.getResponse()
                        .getContentAsByteArray(), CartItemDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(3)
    @DisplayName("""
            Update the book quantity in ShoppingCart when given valid quantity
            """)
    @WithMockUser(username = "user@i.ua")
    @Sql(
            scripts = "classpath:database/cartitems/insert-into-cart_items.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/cartitems/delete-from-cart_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void updateBookInShoppingCart_ValidParams_ReturnsShoppingCartDto() throws Exception {
        //Given
        UpdateCartItemRequestDto requestDto =
                new UpdateCartItemRequestDto(5);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CartItemDto expected = new CartItemDto(
                1L,
                1L,
                "Sample Book 1",
                5);

        //When
        MvcResult result = mockMvc.perform(
                        put("/cart/items/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        //Then
        CartItemDto actual = objectMapper.readValue(
                result.getResponse()
                        .getContentAsByteArray(), CartItemDto.class
        );
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Order(4)
    @DisplayName("""
            Delete book from ShoppingCart when book exists
            """)
    @WithMockUser(username = "user@i.ua")
    @Sql(
            scripts = "classpath:database/cartitems/insert-into-cart_items.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/cartitems/delete-from-cart_items.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void deleteBookFromShoppingCart_ExistingCartItemId_ReturnsNothing() throws Exception {
        //When
        MvcResult result = mockMvc.perform(
                        delete("/cart/items/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        //Then
        String actual = result.getResponse().getContentAsString();
        Assertions.assertTrue(actual.isEmpty());
    }

    private ShoppingCartDto getShoppingCartDto() {
        return new ShoppingCartDto()
                .setId(2L)
                .setUserId(2L)
                .setCartItems(Set.of(getCartItemDto()));
    }

    private CartItemDto getCartItemDto() {
        return new CartItemDto(
                1L,
                1L,
                "Sample Book 1",
                1);
    }

    private CreateCartItemRequestDto getCreateCartItemRequestDto() {
        return new CreateCartItemRequestDto(
                1L,
                1);
    }
}
