package media.samson.controller;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import media.samson.dto.CreateOrderLineItem;
import media.samson.dto.CreateVendor;
import media.samson.dto.CreateVendorPart;
import media.samson.dto.UpdateOrderLineItem;
import media.samson.entity.Order;
import media.samson.entity.OrderLineItem;
import media.samson.entity.Vendor;
import media.samson.entity.VendorPart;
import media.samson.repository.OrderLineItemRepository;
import media.samson.repository.OrderRepository;
import media.samson.repository.VendorPartRepository;
import media.samson.repository.VendorRepository;
import media.samson.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class OrderControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderLineItemRepository orderLineItemRepository;

    @Inject
    OrderService orderService;

    @AfterEach
    public void tearDown() {
        orderLineItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    public void testFindNonExistingReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/order/10000000"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testCreateOrder() {
        HttpRequest<?> createRequest = HttpRequest.POST("/order", Collections.emptyMap());
        HttpResponse<Order> createResponse = client.toBlocking().exchange(createRequest, Order.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatus());
        assertTrue(createResponse.getBody().isPresent());
        Order order = createResponse.getBody().get();
        assertNotNull(order);
        assertEquals(Order.Status.CREATED, order.getStatus());
    }

    @Test
    public void testReadOrder() {
        HttpRequest<?> createRequest = HttpRequest.POST("/order", Collections.emptyMap());
        HttpResponse<Order> createResponse = client.toBlocking().exchange(createRequest, Order.class);
        Order order = createResponse.getBody().get();
        assertNotNull(order);

        HttpRequest<?> readRequest = HttpRequest.GET("/order/" + order.getOrderId());
        Order readResponse = client.toBlocking().retrieve(readRequest, Order.class);
        assertNotNull(readResponse);
        assertEquals(order.getStatus(), readResponse.getStatus());
    }

    @Test
    public void testUpdateOrder() {
        HttpRequest<?> createRequest = HttpRequest.POST("/order", Collections.emptyMap());
        HttpResponse<Order> createResponse = client.toBlocking().exchange(createRequest, Order.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatus());
        Order order = createResponse.getBody().get();
        assertNotNull(order);

        order.setStatus(Order.Status.PENDING);
        HttpRequest<?> updateRequest = HttpRequest.PUT("/order", order);
        HttpResponse<?> updateResponse = client.toBlocking().exchange(updateRequest, Order.class);
        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatus());

        HttpRequest<?> readRequest = HttpRequest.GET("/order/" + order.getOrderId());
        Order readResponse = client.toBlocking().retrieve(readRequest, Order.class);
        assertNotNull(readResponse);
        assertEquals(order.getStatus(), readResponse.getStatus());


    }

    @Test
    public void testDeleteOrder() {
        HttpRequest<?> createRequest = HttpRequest.POST("/order", Collections.emptyMap());
        HttpResponse<Order> createResponse = client.toBlocking().exchange(createRequest, Order.class);
        Order order = createResponse.getBody().get();
        assertNotNull(order);

        HttpRequest<?> deleteRequest = HttpRequest.DELETE("/order/" + order.getOrderId());
        HttpResponse<?> deleteResponse = client.toBlocking().exchange(deleteRequest, Order.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatus());

        var res = assertThrows(HttpClientResponseException.class, () -> {
            HttpRequest<?> readRequest = HttpRequest.GET("/order/" + order.getOrderId());
            Order readResponse = client.toBlocking().retrieve(readRequest, Order.class);
        });

        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    public void testListOrders() {
        HttpRequest<?> createRequest = HttpRequest.POST("/order", Collections.emptyMap());
        HttpResponse<Order> createResponse = client.toBlocking().exchange(createRequest, Order.class);
        Order order = createResponse.getBody().get();
        assertNotNull(order);


        HttpRequest<?> listRequest = HttpRequest.GET("/order");
        List<Order> orders = client.toBlocking().retrieve(listRequest, Argument.of(List.class, Order.class));

        assertEquals(1, orders.size());

        HttpRequest<?> listSizeRequest = HttpRequest.GET("/order?size=1");
        orders = client.toBlocking().retrieve(listSizeRequest, Argument.of(List.class, Order.class));

        assertEquals(1, orders.size());
        assertEquals(Order.Status.CREATED, orders.get(0).getStatus());

        HttpRequest<?> listOrderRequest = HttpRequest.GET("/order?size=1&sort=status,desc");
        orders = client.toBlocking().retrieve(listOrderRequest, Argument.of(List.class, Order.class));

        assertEquals(1, orders.size());

        HttpRequest<?> listPageRequest = HttpRequest.GET("/order?size=1&page=2");
        orders = client.toBlocking().retrieve(listPageRequest, Argument.of(List.class, Order.class));

        assertEquals(0, orders.size());

    }

    @Test
    public void testCreateOrderLineItem() {
        HttpRequest<?> createRequest = HttpRequest.POST("/order", Collections.emptyMap());
        HttpResponse<Order> createResponse = client.toBlocking().exchange(createRequest, Order.class);
        Order order = createResponse.getBody().get();
        assertNotNull(order);

        var initVendor = new CreateVendor("Acme");
        HttpRequest<?> createVendorRequest = HttpRequest.POST("/vendor", initVendor);
        HttpResponse<Vendor> createVendorResponse = client.toBlocking().exchange(createVendorRequest, Vendor.class);
        var createdVendor = createVendorResponse.getBody().get();

        var initPart = new CreateVendorPart(
                "Lemonade",
                "A fizzy lemon flavored drink",
                new BigDecimal("0.99"),
                createdVendor.getVendorId()
        );

        HttpRequest<?> createPartRequest = HttpRequest.POST("/vendor-part", initPart);
        HttpResponse<VendorPart> createPartResponse = client.toBlocking().exchange(createPartRequest, VendorPart.class);
        assertNotNull(createPartResponse);
        assertEquals(HttpStatus.CREATED, createPartResponse.getStatus());
        var vendorPart = createPartResponse.getBody().get();

        HttpRequest<?> createLineItemRequest = HttpRequest.POST(
                "/order/" + order.getOrderId() + "/line-item",
                new CreateOrderLineItem(
                        1,
                        vendorPart.getVendorPartId()
                )
        );
        HttpResponse<OrderLineItem> createLineItemResponse = client.toBlocking().exchange(
                createLineItemRequest,
                OrderLineItem.class
        );
        assertEquals(HttpStatus.CREATED, createLineItemResponse.getStatus());
        assertTrue(createLineItemResponse.getBody().isPresent());
        var orderLineItem = createLineItemResponse.getBody().get();
        assertNotNull(orderLineItem);
        assertEquals(1, orderLineItem.getQuantity());
    }

    @Test
    public void testUpdateOrderLineItem() {
        HttpRequest<?> createRequest = HttpRequest.POST("/order", Collections.emptyMap());
        HttpResponse<Order> createResponse = client.toBlocking().exchange(createRequest, Order.class);
        Order order = createResponse.getBody().get();

        var initVendor = new CreateVendor("Acme");
        HttpRequest<?> createVendorRequest = HttpRequest.POST("/vendor", initVendor);
        HttpResponse<Vendor> createVendorResponse = client.toBlocking().exchange(createVendorRequest, Vendor.class);
        var createdVendor = createVendorResponse.getBody().get();

        var initPart = new CreateVendorPart(
                "Lemonade",
                "A fizzy lemon flavored drink",
                new BigDecimal("0.99"),
                createdVendor.getVendorId()
        );

        HttpRequest<?> createPartRequest = HttpRequest.POST("/vendor-part", initPart);
        HttpResponse<VendorPart> createPartResponse = client.toBlocking().exchange(createPartRequest, VendorPart.class);
        var vendorPart = createPartResponse.getBody().get();

        HttpRequest<?> createLineItemRequest = HttpRequest.POST(
                "/order/" + order.getOrderId() + "/line-item",
                new CreateOrderLineItem(
                        1,
                        vendorPart.getVendorPartId()
                )
        );
        HttpResponse<OrderLineItem> createLineItemResponse = client.toBlocking().exchange(
                createLineItemRequest,
                OrderLineItem.class
        );
        var orderLineItem = createLineItemResponse.getBody().get();

        HttpRequest<?> updateLineItemRequest = HttpRequest.PUT(
                "/order/" + order.getOrderId() + "/line-item",
                new UpdateOrderLineItem(
                        orderLineItem.getOrderLineItemId(),
                        2
                )
        );
        HttpResponse<OrderLineItem> updateLineItemResponse = client.toBlocking().exchange(updateLineItemRequest, OrderLineItem.class);
        assertEquals(HttpStatus.NO_CONTENT, updateLineItemResponse.getStatus());

        HttpRequest<?> listLineItemRequest = HttpRequest.GET("/order/" + order.getOrderId() + "/line-item");
        List<OrderLineItem> listLineItemResponse = client.toBlocking().retrieve(listLineItemRequest, Argument.listOf(OrderLineItem.class));
        assertNotNull(listLineItemResponse);
        var firstLineItem = listLineItemResponse.getFirst();
        assertEquals(2, firstLineItem.getQuantity());
    }

    @Test
    public void testDeleteOrderLineItem() {
        HttpRequest<?> createRequest = HttpRequest.POST("/order", Collections.emptyMap());
        HttpResponse<Order> createResponse = client.toBlocking().exchange(createRequest, Order.class);
        Order order = createResponse.getBody().get();

        var initVendor = new CreateVendor("Acme");
        HttpRequest<?> createVendorRequest = HttpRequest.POST("/vendor", initVendor);
        HttpResponse<Vendor> createVendorResponse = client.toBlocking().exchange(createVendorRequest, Vendor.class);
        var createdVendor = createVendorResponse.getBody().get();

        var initPart = new CreateVendorPart(
                "Lemonade",
                "A fizzy lemon flavored drink",
                new BigDecimal("0.99"),
                createdVendor.getVendorId()
        );

        HttpRequest<?> createPartRequest = HttpRequest.POST("/vendor-part", initPart);
        HttpResponse<VendorPart> createPartResponse = client.toBlocking().exchange(createPartRequest, VendorPart.class);
        var vendorPart = createPartResponse.getBody().get();

        HttpRequest<?> createLineItemRequest = HttpRequest.POST(
                "/order/" + order.getOrderId() + "/line-item",
                new CreateOrderLineItem(
                        1,
                        vendorPart.getVendorPartId()
                )
        );
        HttpResponse<OrderLineItem> createLineItemResponse = client.toBlocking().exchange(
                createLineItemRequest,
                OrderLineItem.class
        );
        var orderLineItem = createLineItemResponse.getBody().get();

        HttpRequest<?> deleteLineItemRequest = HttpRequest.DELETE(
                "/order/" + order.getOrderId() + "/line-item/" + orderLineItem.getOrderLineItemId());
        HttpResponse<?> deleteLineItemResponse = client.toBlocking().exchange(
                deleteLineItemRequest,
                OrderLineItem.class
        );
        assertEquals(HttpStatus.NO_CONTENT, deleteLineItemResponse.getStatus());
    }
}