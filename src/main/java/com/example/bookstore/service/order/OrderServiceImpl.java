package com.example.bookstore.service.order;

import com.example.bookstore.dto.order.CreateOrderRequestDto;
import com.example.bookstore.dto.order.OrderDto;
import com.example.bookstore.dto.order.UpdateOrderRequestDto;
import com.example.bookstore.dto.orderitem.OrderItemDto;
import com.example.bookstore.exception.EntityNotFoundException;
import com.example.bookstore.exception.OrderProcessingException;
import com.example.bookstore.mapper.OrderItemMapper;
import com.example.bookstore.mapper.OrderMapper;
import com.example.bookstore.model.CartItem;
import com.example.bookstore.model.Order;
import com.example.bookstore.model.OrderItem;
import com.example.bookstore.model.ShoppingCart;
import com.example.bookstore.repository.order.OrderRepository;
import com.example.bookstore.repository.orderitem.OrderItemRepository;
import com.example.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookstore.repository.user.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;

    @Override
    public OrderDto createOrder(Long userId, Pageable pageable,
                                CreateOrderRequestDto createOrderDto) {
        ShoppingCart shoppingCart = findShoppingCartByUserId(userId);

        Set<CartItem> cartItems = shoppingCart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new OrderProcessingException(
                    "Unable to create order, add items to shopping cart");
        }

        Order order = placeAnOrder(shoppingCart, createOrderDto.shippingAddress());
        order.setOrderItems(createSetOfOrderItems(order, cartItems));
        OrderDto orderResponseDto = orderMapper.toDto(order);
        cleanShoppingCart(shoppingCart);

        return orderResponseDto;
    }

    @Override
    public List<OrderDto> getOrders(Long userId, Pageable pageable) {
        List<Order> userOrders = orderRepository.findAllByUserId(userId, pageable);
        if (userOrders.isEmpty()) {
            throw new EntityNotFoundException(
                    "No orders found for user with id: " + userId);
        }

        return orderMapper.toDtoList(userOrders);
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId,
                                      UpdateOrderRequestDto updateOrderDto) {
        Order order = findOrderByOrderId(orderId);
        order.setStatus(updateOrderDto.status());
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public List<OrderItemDto> getOrderItemsByOrderId(Long orderId,
                                                     Pageable pageable) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId, pageable);

        return orderItemMapper.toDtoList(orderItems);
    }

    @Override
    public OrderItemDto getOrderItemByIdAndOrderId(Long orderId,
                                                   Long orderItemId) {
        OrderItem orderItem = findOrderItemByOrderItemId(orderItemId, orderId);
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new OrderProcessingException(
                    "OrderItem does not belong to the specified Order with id: " + orderId);
        }
        return orderItemMapper.toDto(orderItem);
    }

    private ShoppingCart findShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find shopping cart by user Id: " + userId));
    }

    private Order findOrderByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find order by order id: " + orderId));
    }

    private OrderItem findOrderItemByOrderItemId(Long orderItemId, Long orderId) {
        return orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find order item by id: " + orderId));
    }

    private Order placeAnOrder(ShoppingCart shoppingCart, String shippingAddress) {
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setTotal(countTotalOrderPrice(shoppingCart.getCartItems()));
        return orderRepository.save(order);
    }

    private BigDecimal countTotalOrderPrice(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(c -> c.getBook()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(c.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<OrderItem> createSetOfOrderItems(Order order, Set<CartItem> cartItems) {
        Set<OrderItem> orderItems = cartItems.stream()
                .map(c -> {
                    OrderItem orderItem = orderItemMapper.toEntityFromCartItem(c);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());
        orderItemRepository.saveAll(orderItems);
        return orderItems;
    }

    private void cleanShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
    }
}
