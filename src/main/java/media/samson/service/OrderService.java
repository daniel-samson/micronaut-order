package media.samson.service;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import media.samson.dto.CreateOrderLineItem;
import media.samson.dto.UpdateOrder;
import media.samson.dto.UpdateOrderLineItem;
import media.samson.entity.Order;
import media.samson.entity.OrderLineItem;
import media.samson.repository.OrderLineItemRepository;
import media.samson.repository.OrderRepository;
import media.samson.repository.VendorPartRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Singleton
public class OrderService {
    @Inject
    private OrderRepository orderRepository;

    @Inject
    private OrderLineItemRepository orderLineItemRepository;

    @Inject
    VendorPartRepository vendorPartRepository;

    public List<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).getContent();
    }

    public Order createOrder() {
        return orderRepository.create(new Order());
    }

    public Optional<Order> readOrder(BigInteger orderId) {
        return orderRepository.findById(orderId);
    }

    public void updateOrder(UpdateOrder order) {
        orderRepository.update(
                new Order(
                        order.orderId(),
                        order.status()
                )
        );
    }

    public void deleteOrder(BigInteger orderId) {
        orderRepository.deleteById(orderId);
    }

    @Transactional
    public List<OrderLineItem> getOrderLineItems(BigInteger orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST, "Order not found"));

        return orderLineItemRepository.findByOrder(order);
    }

    public OrderLineItem createOrderLineItem(CreateOrderLineItem createOrderLineItem) {
        var order = orderRepository.findById(createOrderLineItem.orderId())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST, "Order not found"));

        var vendorPart = vendorPartRepository.findById(createOrderLineItem.vendorPartId())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST, "VendorPart not found"));

        var lineItem = new OrderLineItem(
                createOrderLineItem.quantity(),
                order,
                vendorPart
        );

        return orderLineItemRepository.create(lineItem);
    }

    public void updateOrderLineItem(UpdateOrderLineItem updateOrderLineItem) {
        var lineItem = orderLineItemRepository.findById(updateOrderLineItem.orderLineItemId())
                .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST, "Order line item not found"));

        lineItem.setQuantity(updateOrderLineItem.quantity());

        orderLineItemRepository.update(lineItem);
    }

    @Transactional
    public void deleteOrderLineItem(BigInteger lineItemId) {
        var lineItem = orderLineItemRepository.findById(lineItemId)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.BAD_REQUEST, "Order line item not found"));

        orderLineItemRepository.delete(lineItem);
    }
}
