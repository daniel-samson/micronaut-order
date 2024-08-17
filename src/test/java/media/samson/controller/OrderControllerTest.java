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
import media.samson.entity.Order;
import media.samson.entity.OrderLineItem;
import media.samson.entity.VendorPart;
import media.samson.repository.VendorPartRepository;
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
    VendorPartRepository vendorPartRepository;

    @Test
    public void testFindNonExistingReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/order/10000000"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testGenreCrudOperations() {

        List<BigInteger> orderIds = new ArrayList<>();

        HttpRequest<?> createRequest = HttpRequest.POST("/order", Collections.singletonMap("status", Order.Status.PENDING.name()));
        HttpResponse<Order> createdResponse = client.toBlocking().exchange(createRequest, Order.class);

        assertEquals(HttpStatus.CREATED, createdResponse.getStatus());
        assertFalse(createdResponse.getBody().isEmpty());

        BigInteger id = createdResponse.getBody().get().getOrderId();
        orderIds.add(id);

        HttpRequest<?> readRequest = HttpRequest.GET("/order/" + id);
        Order order = client.toBlocking().retrieve(readRequest, Order.class);

        assertEquals(Order.Status.PENDING, order.getStatus());

        HttpRequest<?> updateRequest = HttpRequest.PUT("/order", new Order(id, Order.Status.SHIPPED, new ArrayList<OrderLineItem>()));
        HttpResponse<?> updateResponse = client.toBlocking().exchange(updateRequest);

        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatus());

        HttpRequest<?> read2Request = HttpRequest.GET("/order/" + id);
        order = client.toBlocking().retrieve(read2Request, Order.class);

        assertEquals(id, order.getOrderId());
        assertEquals(Order.Status.SHIPPED, order.getStatus());

        HttpRequest<?> listRequest = HttpRequest.GET("/order");
        List<Order> orders = client.toBlocking().retrieve(listRequest, Argument.of(List.class, Order.class));

        assertEquals(1, orders.size());

        HttpRequest<?> listSizeRequest = HttpRequest.GET("/order?size=1");
        orders = client.toBlocking().retrieve(listSizeRequest, Argument.of(List.class, Order.class));

        assertEquals(1, orders.size());
        assertEquals(Order.Status.SHIPPED, orders.get(0).getStatus());

        HttpRequest<?> listOrderRequest = HttpRequest.GET("/order?size=1&sort=status,desc");
        orders = client.toBlocking().retrieve(listOrderRequest, Argument.of(List.class, Order.class));

        assertEquals(1, orders.size());

        HttpRequest<?> listPageRequest = HttpRequest.GET("/order?size=1&page=2");
        orders = client.toBlocking().retrieve(listPageRequest, Argument.of(List.class, Order.class));

        assertEquals(0, orders.size());

        // cleanup:
        for (BigInteger orderId : orderIds) {
            HttpRequest<?> request = HttpRequest.DELETE("/order/" + orderId);
            HttpResponse<?> response = client.toBlocking().exchange(request);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        }
    }

    @Test
    public void testOrderLineItemsCrud() {
        var lemonadePart = vendorPartRepository.create(new VendorPart("Lemonade", "A fizzy lemon flavored drink", new BigDecimal("0.99")));
        var colaPart = vendorPartRepository.create(new VendorPart("Cola", "A fizzy lemon drink", new BigDecimal("0.99")));

        ArrayList<OrderLineItem> initialLineItems = new ArrayList<OrderLineItem>();
        initialLineItems.add(new OrderLineItem(1, lemonadePart));
        HttpRequest<?> createRequest = HttpRequest.POST(
                "/order",
                new Order(null, Order.Status.PENDING, initialLineItems));
        HttpResponse<Order> createdResponse = client.toBlocking().exchange(createRequest, Order.class);
        assertEquals(HttpStatus.CREATED, createdResponse.getStatus());
        Order order = createdResponse.getBody().get();
        assertEquals(1, order.getLineItems().size());
        assertEquals(1, order.getLineItems().getFirst().getQuantity());

        order.getLineItems().add(new OrderLineItem(2, colaPart));
        HttpRequest<?> updateRequest = HttpRequest.PUT("/order", order);
        HttpResponse<?> updatedResponse = client.toBlocking().exchange(updateRequest, Order.class);
        assertEquals(HttpStatus.NO_CONTENT, updatedResponse.getStatus());

        HttpRequest<?> readRequest = HttpRequest.GET("/order/" + order.getOrderId());
        Order readResponse = client.toBlocking().retrieve(readRequest, Order.class);

        assertNotEquals(null, readResponse);
        assertEquals(2, readResponse.getLineItems().size());

        var url = "/order/" + order.getOrderId() + "/line-item/" + order.getLineItems().getFirst().getOrderLineItemId();
        HttpRequest<?> deleteRequest = HttpRequest.DELETE(url);
        HttpResponse<?> deleteResponse = client.toBlocking().exchange(deleteRequest);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatus());

        HttpRequest<?> read2Request = HttpRequest.GET("/order/" + order.getOrderId());
        Order read2Response = client.toBlocking().retrieve(readRequest, Order.class);

        assertNotEquals(null, read2Response);
        assertEquals(1, read2Response.getLineItems().size());
        assertEquals(2, read2Response.getLineItems().getFirst().getQuantity());
    }
}