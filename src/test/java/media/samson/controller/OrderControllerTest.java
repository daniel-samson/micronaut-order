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
import org.junit.jupiter.api.Test;

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

    @Test
    public void testFindNonExistingGenreReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/order/10000000"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testGenreCrudOperations() {

        List<BigInteger> orderIds = new ArrayList<>();

        HttpRequest<?> request = HttpRequest.POST("/order", Collections.singletonMap("status", Order.Status.PENDING.name()));
        HttpResponse<Order> createdResponse = client.toBlocking().exchange(request, Order.class);

        assertEquals(HttpStatus.CREATED, createdResponse.getStatus());
        assertFalse(createdResponse.getBody().isEmpty());

        BigInteger id = createdResponse.getBody().get().getOrderId();
        orderIds.add(id);

        request = HttpRequest.GET("/order/" + id);
        Order order = client.toBlocking().retrieve(request, Order.class);

        assertEquals(Order.Status.PENDING, order.getStatus());

        request = HttpRequest.PUT("/order", new Order(id, Order.Status.SHIPPED, new ArrayList<OrderLineItem>()));
        HttpResponse<?> response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        request = HttpRequest.GET("/order/" + id);
        order = client.toBlocking().retrieve(request, Order.class);

        assertEquals(id, order.getOrderId());
        assertEquals(Order.Status.SHIPPED, order.getStatus());

        request = HttpRequest.GET("/order");
        List<Order> orders = client.toBlocking().retrieve(request, Argument.of(List.class, Order.class));

        assertEquals(1, orders.size());

        request = HttpRequest.GET("/order?size=1");
        orders = client.toBlocking().retrieve(request, Argument.of(List.class, Order.class));

        assertEquals(1, orders.size());
        assertEquals(Order.Status.SHIPPED, orders.get(0).getStatus());

        request = HttpRequest.GET("/order?size=1&sort=status,desc");
        orders = client.toBlocking().retrieve(request, Argument.of(List.class, Order.class));

        assertEquals(1, orders.size());

        request = HttpRequest.GET("/order?size=1&page=2");
        orders = client.toBlocking().retrieve(request, Argument.of(List.class, Order.class));

        assertEquals(0, orders.size());

        // cleanup:
        for (BigInteger orderId : orderIds) {
            request = HttpRequest.DELETE("/order/" + orderId);
            response = client.toBlocking().exchange(request);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        }
    }

    @Test
    public void testOrderLineItemsCrud() {
        ArrayList<OrderLineItem> initialLineItems = new ArrayList<OrderLineItem>();
        initialLineItems.add(new OrderLineItem(1));
        HttpRequest<?> createRequest = HttpRequest.POST(
                "/order",
                new Order(null, Order.Status.PENDING, initialLineItems));
        HttpResponse<Order> createdResponse = client.toBlocking().exchange(createRequest, Order.class);
        assertEquals(HttpStatus.CREATED, createdResponse.getStatus());
        Order order = createdResponse.getBody().get();
        assertEquals(1, order.getLineItems().size());
        assertEquals(1, order.getLineItems().getFirst().getQuantity());

        order.getLineItems().add(new OrderLineItem(2));
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