package com.example.bookstore.controller;

import com.example.bookstore.dto.order.CreateOrderRequestDto;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.UpdateOrderRequestDto;
import com.example.bookstore.dto.orderitem.OrderItemDto;
import com.example.bookstore.model.User;
import com.example.bookstore.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Orders management", description = "Endpoint for managing orders")
@Validated
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit current order",
            description = "Submitting current creating order")
    @PreAuthorize("hasRole('USER')")
    public OrderDto submitOrder(@RequestBody @Valid CreateOrderRequestDto requestDto,
                                @ParameterObject
                                @PageableDefault(
                                        size = 5,
                                        sort = "userId",
                                        direction = Sort.Direction.ASC)
                                Pageable pageable,
                                @AuthenticationPrincipal User user) {
        return orderService.createOrder(user.getId(), pageable, requestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user orders",
            description = "Getting all user orders")
    @PreAuthorize("hasRole('USER')")
    public List<OrderDto> getOrders(@ParameterObject
                                    @PageableDefault(
                                            size = 5,
                                            sort = "userId",
                                            direction = Sort.Direction.ASC)
                                    Pageable pageable) {
        Long userId = getCurrentUserId();
        return orderService.getOrders(userId, pageable);
    }

    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update order status",
            description = "Updating order status by orderId")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDto updateOrderStatus(@PathVariable @Positive Long orderId,
                                      @RequestBody @Valid UpdateOrderRequestDto requestDto) {
        return orderService.updateOrderStatus(orderId, requestDto);
    }

    @GetMapping("/{orderId}/items")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get items from order",
            description = "Getting items from order by orderId")
    @PreAuthorize("hasRole('USER')")
    public List<OrderItemDto> getOrderItemsByOrderId(@PathVariable @Positive Long orderId,
                                                     @ParameterObject
                                                     @PageableDefault(
                                                             size = 5,
                                                             sort = "bookId",
                                                             direction = Sort.Direction.ASC)
                                                     Pageable pageable) {
        return orderService.getOrderItemsByOrderId(orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{orderItemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get order items",
            description = "Getting order items by orderId and ItemId")
    @PreAuthorize("hasRole('USER')")
    public OrderItemDto getOrderItemByIdAndOrderId(@PathVariable @Positive Long orderId,
                                                   @PathVariable @Positive Long orderItemId) {
        return orderService.getOrderItemByIdAndOrderId(orderId, orderItemId);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
