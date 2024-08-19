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
import media.samson.entity.Vendor;
import media.samson.repository.VendorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class VendorControllerTest {
    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    VendorRepository vendorRepository;

    @AfterEach
    public void tearDown() {
        vendorRepository.deleteAll();
    }

    @Test
    public void testFindNonExistingReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/vendor/10000000"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testCreateValidVendor() {
        var initVendor = new Vendor("Acme");

        HttpRequest<?> request = HttpRequest.POST("/vendor", initVendor);
        HttpResponse<Vendor> response = client.toBlocking().exchange(request, Vendor.class);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getBody());

        var createdVendor = response.getBody().get();
        assertNotNull(createdVendor.getVendorId());
        assertEquals(1, createdVendor.getVendorId().compareTo(new BigInteger("0")));
        assertEquals("Acme", createdVendor.getName());
    }

    @Test
    public void testCreateInvalidVendor() {
        HttpRequest<?> request = HttpRequest.POST(
                "/vendor"
                , Collections.singletonMap("vendorId", new BigInteger("12")));

        assertThrows(HttpClientResponseException.class, () -> {
            HttpResponse<?> response = client.toBlocking().exchange(request, Vendor.class);
            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        });

    }

    @Test
    public void testReadVendor() {
        var initVendor = new Vendor("Acme Inc.");
        HttpRequest<?> createdRequest = HttpRequest.POST("/vendor", initVendor);
        HttpResponse<Vendor> createdResponse = client.toBlocking().exchange(createdRequest, Vendor.class);
        var createdVendor = createdResponse.getBody().get();

        createdVendor.setName("Acme Inc.");
        HttpRequest<?> request = HttpRequest.GET("/vendor/" + createdVendor.getVendorId());
        HttpResponse<Vendor> response = client.toBlocking().exchange(request, Vendor.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertFalse(response.getBody().isEmpty());
        var readVendor = response.getBody().get();
        assertNotNull(readVendor.getVendorId());
        assertEquals(1, readVendor.getVendorId().compareTo(new BigInteger("0")));
        assertEquals("Acme Inc.", readVendor.getName());
    }

    @Test
    public void testUpdateVendor() {
        var initVendor = new Vendor("Acme");
        HttpRequest<?> createdRequest = HttpRequest.POST("/vendor", initVendor);
        HttpResponse<Vendor> createdResponse = client.toBlocking().exchange(createdRequest, Vendor.class);
        var createdVendor = createdResponse.getBody().get();

        createdVendor.setName("Acme Inc.");
        HttpRequest<?> updateRequest = HttpRequest.PUT("/vendor", createdVendor);
        HttpResponse<Vendor> updateResponse = client.toBlocking().exchange(updateRequest, Vendor.class);
        assertNotNull(updateResponse);
        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatus());
        assertTrue(updateResponse.getBody().isEmpty());

        HttpRequest<?> request = HttpRequest.GET("/vendor/" + createdVendor.getVendorId());
        HttpResponse<Vendor> response = client.toBlocking().exchange(request, Vendor.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertFalse(response.getBody().isEmpty());

        var updatedVendor = response.getBody().get();
        assertNotNull(updatedVendor.getVendorId());
        assertEquals(1, updatedVendor.getVendorId().compareTo(new BigInteger("0")));
        assertEquals("Acme Inc.", updatedVendor.getName());
    }

    @Test
    public void testDeleteVendor() {
        var initVendor = new Vendor("Acme");
        HttpRequest<?> createdRequest = HttpRequest.POST("/vendor", initVendor);
        HttpResponse<Vendor> createdResponse = client.toBlocking().exchange(createdRequest, Vendor.class);
        var createdVendor = createdResponse.getBody().get();

        HttpRequest<?> request = HttpRequest.DELETE("/vendor/" + createdVendor.getVendorId());
        HttpResponse<Vendor> response = client.toBlocking().exchange(request, Vendor.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        assertThrows(HttpClientResponseException.class, () -> {
            HttpRequest<?> readRequest = HttpRequest.GET("/vendor/" + createdVendor.getVendorId());
            HttpResponse<Vendor> readResponse = client.toBlocking().exchange(readRequest, Vendor.class);
            assertNotNull(readResponse);
            assertEquals(HttpStatus.NOT_FOUND, readResponse.getStatus());
        });
    }

    @Test
    public void testListVendors() {
        var initVendor = new Vendor("Acme");
        HttpRequest<?> createdRequest = HttpRequest.POST("/vendor", initVendor);
        HttpResponse<Vendor> createdResponse = client.toBlocking().exchange(createdRequest, Vendor.class);

        HttpRequest<?> listRequest = HttpRequest.GET("/vendor");
        List<Vendor> vendors = client.toBlocking().retrieve(listRequest, Argument.of(List.class, Vendor.class));

        assertEquals(1, vendors.size());

        HttpRequest<?> listSizeRequest = HttpRequest.GET("/vendor?size=1");
        vendors = client.toBlocking().retrieve(listSizeRequest, Argument.of(List.class, Vendor.class));

        assertEquals(1, vendors.size());
        assertEquals("Acme", vendors.get(0).getName());

        HttpRequest<?> listOrderRequest = HttpRequest.GET("/vendor?size=1&sort=name,desc");
        vendors = client.toBlocking().retrieve(listOrderRequest, Argument.of(List.class, Vendor.class));

        assertEquals(1, vendors.size());

        HttpRequest<?> listPageRequest = HttpRequest.GET("/vendor?size=1&page=2");
        vendors = client.toBlocking().retrieve(listPageRequest, Argument.of(List.class, Vendor.class));

        assertEquals(0, vendors.size());
    }
}
