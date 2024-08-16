package media.samson.controller;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import media.samson.entity.Order;
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

        request = HttpRequest.PUT("/order", new Order(id, Order.Status.SHIPPED));
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
}