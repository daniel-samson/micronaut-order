package media.samson.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import media.samson.dto.*;
import media.samson.entity.Order;
import media.samson.entity.OrderLineItem;
import media.samson.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class OrderServiceTest {
    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderService orderService;

    @Inject
    VendorService vendorService;

    @Inject
    VendorPartService vendorPartService;

    @AfterEach
    public void tearDown() {
        orderRepository.deleteAll();
    }

    @Test
    public void testCreateOrder() {
        var order = orderService.createOrder();
        assertEquals(Order.Status.CREATED, order.getStatus());
    }

    @Test
    public void testReadOrder() {
        var order = orderService.createOrder();
        var readOrder = orderService.readOrder(order.getOrderId());

        assertTrue(readOrder.isPresent());
        assertEquals(order.getOrderId(), readOrder.get().getOrderId());
        assertEquals(order.getStatus(), readOrder.get().getStatus());
    }

    @Test
    public void testUpdateOrder() {
        var order = orderService.createOrder();

        order.setStatus(Order.Status.PENDING);
        orderService.updateOrder(
                new UpdateOrder(
                        order.getOrderId(),
                        order.getStatus()
                )
        );
        var updatedOrder = orderService.readOrder(order.getOrderId());
        assertTrue(updatedOrder.isPresent());
        assertEquals(Order.Status.PENDING, updatedOrder.get().getStatus());
    }

    @Test
    public void testDeleteOrder() {
        var order = orderService.createOrder();
        orderService.deleteOrder(order.getOrderId());
        var missingOrder = orderService.readOrder(order.getOrderId());
        assertTrue(missingOrder.isEmpty());
    }

    @Test
    public void testCreateOrderLineItem() {
        var vendor = vendorService.createVendor(
                new CreateVendor("Acme")
        );

        var vendorPart = vendorPartService.createVendorPart(
                new CreateVendorPart(
                        "Lemonade",
                        "Fizzy lemon flavored Drink",
                        BigDecimal.ONE,
                        vendor.getVendorId()
                )
        );

        var order = orderService.createOrder();
        var lineItem = orderService.createOrderLineItem(
                order.getOrderId(),
                new CreateOrderLineItem(1, vendorPart.getVendorPartId())
        );

        assertEquals(1, lineItem.getQuantity());
        assertEquals(order.getOrderId(), lineItem.getOrder().getOrderId());
        assertEquals(vendorPart.getVendorPartId(), lineItem.getVendorPart().getVendorPartId());
    }

    @Test
    public void testUpdateOrderLineItem() {
        var vendor = vendorService.createVendor(
                new CreateVendor("Acme")
        );

        var vendorPart = vendorPartService.createVendorPart(
                new CreateVendorPart(
                        "Lemonade",
                        "Fizzy lemon flavored Drink",
                        BigDecimal.ONE,
                        vendor.getVendorId()
                )
        );

        var order = orderService.createOrder();
        var lineItem = orderService.createOrderLineItem(
                order.getOrderId(),
                new CreateOrderLineItem(1, vendorPart.getVendorPartId())
        );

        orderService.updateOrderLineItem(
                order.getOrderId(),
                new UpdateOrderLineItem(lineItem.getOrderLineItemId(), 2)
        );
        var lineItems = orderService.getOrderLineItems(order.getOrderId());
        assertFalse(lineItems.isEmpty());

        var updatedLineItem = lineItems.getFirst();
        assertEquals(2, updatedLineItem.getQuantity());
    }

    @Test
    public void testDeleteOrderLineItem() {
        var vendor = vendorService.createVendor(
                new CreateVendor("Acme")
        );

        var vendorPart = vendorPartService.createVendorPart(
                new CreateVendorPart(
                        "Lemonade",
                        "Fizzy lemon flavored Drink",
                        BigDecimal.ONE,
                        vendor.getVendorId()
                )
        );

        var order = orderService.createOrder();
        var lineItem = orderService.createOrderLineItem(
                order.getOrderId(),
                new CreateOrderLineItem(1, vendorPart.getVendorPartId())
        );

       orderService.deleteOrderLineItem(order.getOrderId(), lineItem.getOrderLineItemId());
        var lineItems = orderService.getOrderLineItems(order.getOrderId());
        assertTrue(lineItems.isEmpty());
    }
}
