package com.example.booknest.mapper;

import com.example.booknest.config.MapperConfig;
import com.example.booknest.dto.orderitem.OrderItemDto;
import com.example.booknest.model.CartItem;
import com.example.booknest.model.OrderItem;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    OrderItemDto toDto(OrderItem orderItem);

    List<OrderItemDto> toDtoList(List<OrderItem> orderItems);

    @Mapping(source = "book.price", target = "price")
    @Mapping(target = "id", ignore = true)
    OrderItem toEntityFromCartItem(CartItem cartItem);
}
