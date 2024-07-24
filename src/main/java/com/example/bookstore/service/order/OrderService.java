package com.example.bookstore.service.order;

import com.example.bookstore.dto.order.CreateOrderRequestDto;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.UpdateOrderRequestDto;
import com.example.bookstore.dto.orderitem.OrderItemDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(Long userId,
                         Pageable pageable,
                         CreateOrderRequestDto createOrderDto);

    List<OrderDto> getOrders(Long userId,
                             Pageable pageable);

    OrderDto updateOrderStatus(Long orderId,
                               UpdateOrderRequestDto updateOrderDto);

    List<OrderItemDto> getOrderItemsByOrderId(Long orderId,
                                              Pageable pageable);

    OrderItemDto getOrderItemByIdAndOrderId(Long orderId,
                                            Long orderItemId);
}
