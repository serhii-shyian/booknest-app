package com.example.booknest.mapper;

import com.example.booknest.config.MapperConfig;
import com.example.booknest.dto.shoppingcart.ShoppingCartDto;
import com.example.booknest.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "cartItems", ignore = true)
    ShoppingCartDto toDto(ShoppingCart shoppingCart);
}
