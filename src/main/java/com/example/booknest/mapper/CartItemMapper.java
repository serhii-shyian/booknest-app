package com.example.booknest.mapper;

import com.example.booknest.config.MapperConfig;
import com.example.booknest.dto.cartitem.CartItemDto;
import com.example.booknest.dto.cartitem.CreateCartItemRequestDto;
import com.example.booknest.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    CartItem toEntity(CreateCartItemRequestDto cartItemDto);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);
}
