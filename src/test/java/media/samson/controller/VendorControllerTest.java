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
import media.samson.entity.*;
import media.samson.entity.Vendor;
import media.samson.entity.Vendor;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class VendorControllerTest {
    @Inject
    @Client("/")
    HttpClient client;

    @Test
    public void testFindNonExistingReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/vendor/10000000"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testCrudOperations() {
        List<BigInteger> vendorIds = new ArrayList<>();

        HttpRequest<?> createRequest = HttpRequest.POST("/vendor", Collections.singletonMap("name", "Acme"));
        HttpResponse<Vendor> createdResponse = client.toBlocking().exchange(createRequest, Vendor.class);

        assertEquals(HttpStatus.CREATED, createdResponse.getStatus());
        assertFalse(createdResponse.getBody().isEmpty());

        BigInteger id = createdResponse.getBody().get().getVendorId();
        vendorIds.add(id);

        HttpRequest<?> readRequest = HttpRequest.GET("/vendor/" + id);
        Vendor vendor = client.toBlocking().retrieve(readRequest, Vendor.class);

        assertEquals("Acme", vendor.getName());

        HttpRequest<?> updateRequest = HttpRequest.PUT("/vendor", new Vendor(id, "Acme Inc."));
        HttpResponse<?> updateResponse = client.toBlocking().exchange(updateRequest);

        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatus());

        HttpRequest<?> read2Request = HttpRequest.GET("/vendor/" + id);
        vendor = client.toBlocking().retrieve(read2Request, Vendor.class);

        assertEquals(id, vendor.getVendorId());
        assertEquals("Acme Inc.", vendor.getName());

        HttpRequest<?> listRequest = HttpRequest.GET("/vendor");
        List<Vendor> vendors = client.toBlocking().retrieve(listRequest, Argument.of(List.class, Vendor.class));

        assertEquals(1, vendors.size());

        HttpRequest<?> listSizeRequest = HttpRequest.GET("/vendor?size=1");
        vendors = client.toBlocking().retrieve(listSizeRequest, Argument.of(List.class, Vendor.class));

        assertEquals(1, vendors.size());
        assertEquals("Acme Inc.", vendors.get(0).getName());

        HttpRequest<?> listOrderRequest = HttpRequest.GET("/vendor?size=1&sort=name,desc");
        vendors = client.toBlocking().retrieve(listOrderRequest, Argument.of(List.class, Vendor.class));

        assertEquals(1, vendors.size());

        HttpRequest<?> listPageRequest = HttpRequest.GET("/vendor?size=1&page=2");
        vendors = client.toBlocking().retrieve(listPageRequest, Argument.of(List.class, Vendor.class));

        assertEquals(0, vendors.size());

        // cleanup:
        for (BigInteger orderId : vendorIds) {
            HttpRequest<?> request = HttpRequest.DELETE("/vendor/" + orderId);
            HttpResponse<?> response = client.toBlocking().exchange(request);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        }

    }
}
