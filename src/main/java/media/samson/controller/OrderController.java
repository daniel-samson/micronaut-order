package media.samson.controller;

import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import media.samson.dto.CreateOrderLineItem;
import media.samson.dto.UpdateOrder;
import media.samson.dto.UpdateOrderLineItem;
import media.samson.entity.Order;
import media.samson.entity.OrderLineItem;
import media.samson.service.OrderService;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Controller("/order")
public class OrderController {
    @Inject
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Get
    public List<Order> index(@Valid Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    @Post
    public HttpResponse<?> create() {
        return HttpResponse.created(orderService.createOrder());
    }

    @Get("/{orderId}")
    public Optional<Order> read(BigInteger orderId) {
        return orderService.readOrder(orderId);
    }

    @Put
    @Status(HttpStatus.NO_CONTENT)
    public void update(@Body UpdateOrder order) {
        orderService.updateOrder(order);
    }

    @Delete("/{orderId}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(BigInteger orderId) {
        orderService.deleteOrder(orderId);
    }

    @Get("/{orderId}/line-item")
    public List<OrderLineItem> getLineItems(BigInteger orderId) {
        return orderService.getOrderLineItems(orderId);
    }


    @Post("/{orderId}/line-item")
    public HttpResponse<OrderLineItem> createLineItem(BigInteger orderId, @Body CreateOrderLineItem createOrderLineItem) {
        return HttpResponse.created(orderService.createOrderLineItem(orderId, createOrderLineItem));
    }

    @Put("/{orderId}/line-item")
    @Status(HttpStatus.NO_CONTENT)
    public void updateLineItem(BigInteger orderId, @Body UpdateOrderLineItem updateOrderLineItem) {
        orderService.updateOrderLineItem(orderId, updateOrderLineItem);
    }

    @Delete("/{orderId}/line-item/{orderLineItemId}")
    @Status(HttpStatus.NO_CONTENT)
    public void deleteLineItem(BigInteger orderId, BigInteger orderLineItemId) {
        orderService.deleteOrderLineItem(orderId, orderLineItemId);
    }
}