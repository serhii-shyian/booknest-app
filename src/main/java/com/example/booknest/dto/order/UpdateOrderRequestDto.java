package com.example.booknest.dto.order;

import com.example.booknest.model.Order;

public record UpdateOrderRequestDto(
        Order.Status status) {
}
