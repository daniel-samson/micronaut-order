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
import media.samson.dto.CreateVendor;
import media.samson.dto.CreateVendorPart;
import media.samson.dto.UpdateVendorPart;
import media.samson.entity.Vendor;
import media.samson.entity.VendorPart;
import media.samson.repository.VendorPartRepository;
import media.samson.repository.VendorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class VendorPartControllerTest {
    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    VendorPartRepository vendorPartRepository;

    @Inject
    VendorRepository vendorRepository;

    @AfterEach
    public void tearDown() {
        vendorPartRepository.deleteAll();
        vendorRepository.deleteAll();
    }

    @Test
    public void testFindNonExistingReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/vendor-part/10000000"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testCreateValidVendorPart() {
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
        assertNotNull(createPartResponse.getBody());
        var createdPart = createPartResponse.getBody().get();
        assertNotNull(createdPart.getVendorPartId());
        assertEquals(1, createdPart.getVendorPartId().compareTo(new BigInteger("0")));
        assertEquals("Lemonade", createdPart.getPartName());
        assertEquals("A fizzy lemon flavored drink", createdPart.getPartDescription());
        assertEquals(new BigDecimal("0.99"), createdPart.getPartPrice());
        assertEquals(createdVendor.getVendorId(), createdPart.getVendor().getVendorId());
    }

    @Test
    public void testCreateInvalidVendorPart() {
        HttpRequest<?> request = HttpRequest.POST(
                "/vendor-part"
,                Collections.singletonMap("vendorPartId", new BigInteger("12")));

        var response = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request, Vendor.class);
        });

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());

    }

    @Test
    public void testReadVendorPart() {
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


        HttpRequest<?> createdRequest = HttpRequest.POST("/vendor-part", initPart);
        HttpResponse<VendorPart> createdResponse = client.toBlocking().exchange(createdRequest, VendorPart.class);
        var createdVendorPart = createdResponse.getBody().get();

        HttpRequest<?> request = HttpRequest.GET("/vendor-part/" + createdVendorPart.getVendorPartId());
        HttpResponse<VendorPart> response = client.toBlocking().exchange(request, VendorPart.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertFalse(response.getBody().isEmpty());
        var readVendor = response.getBody().get();
        assertNotNull(readVendor.getVendorPartId());
        assertEquals(1, readVendor.getVendorPartId().compareTo(new BigInteger("0")));
        assertEquals("Lemonade", readVendor.getPartName());
        assertEquals("A fizzy lemon flavored drink", readVendor.getPartDescription());
        assertEquals(new BigDecimal("0.99"), readVendor.getPartPrice());
        assertEquals(createdVendor.getVendorId(), readVendor.getVendor().getVendorId());
    }

    @Test
    public void testUpdateVendorPart() {
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


        HttpRequest<?> createdRequest = HttpRequest.POST("/vendor-part", initPart);
        HttpResponse<VendorPart> createdResponse = client.toBlocking().exchange(createdRequest, VendorPart.class);
        var createdVendorPart = createdResponse.getBody().get();

        var updatePart = new UpdateVendorPart(
                createdVendorPart.getVendorPartId(),
                "Pink Lemonade",
                initPart.partDescription(),
                initPart.partPrice(),
                initPart.vendorId()
        );
        createdVendorPart.setPartName("Pink Lemonade");
        HttpRequest<?> updateRequest = HttpRequest.PUT("/vendor-part", updatePart);
        HttpResponse<VendorPart> updateResponse = client.toBlocking().exchange(updateRequest, VendorPart.class);
        assertNotNull(updateResponse);
        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatus());
        assertTrue(updateResponse.getBody().isEmpty());

        HttpRequest<?> request = HttpRequest.GET("/vendor-part/" + createdVendorPart.getVendorPartId());
        HttpResponse<VendorPart> response = client.toBlocking().exchange(request, VendorPart.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatus());
        assertFalse(response.getBody().isEmpty());

        var updatedVendor = response.getBody().get();
        assertNotNull(updatedVendor.getVendorPartId());
        assertEquals(1, updatedVendor.getVendorPartId().compareTo(new BigInteger("0")));
        assertEquals("Pink Lemonade", updatedVendor.getPartName());
        assertEquals("A fizzy lemon flavored drink", updatedVendor.getPartDescription());
        assertEquals(new BigDecimal("0.99"), updatedVendor.getPartPrice());
        assertEquals(createdVendor.getVendorId(), updatedVendor.getVendor().getVendorId());
    }

    @Test
    public void testDeleteVendorPart() {
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


        HttpRequest<?> createdRequest = HttpRequest.POST("/vendor-part", initPart);
        HttpResponse<VendorPart> createdResponse = client.toBlocking().exchange(createdRequest, VendorPart.class);
        var createdVendorPart = createdResponse.getBody().get();

        HttpRequest<?> request = HttpRequest.DELETE("/vendor-part/" + createdVendorPart.getVendorPartId());
        HttpResponse<VendorPart> response = client.toBlocking().exchange(request, VendorPart.class);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());

        assertThrows(HttpClientResponseException.class, () -> {
            HttpRequest<?> readRequest = HttpRequest.GET("/vendor-part/" + createdVendorPart.getVendorPartId());
            HttpResponse<VendorPart> readResponse = client.toBlocking().exchange(readRequest, VendorPart.class);
            assertNotNull(readResponse);
            assertEquals(HttpStatus.NOT_FOUND, readResponse.getStatus());
        });
    }

    @Test
    public void testListVendorsPart() {
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

        HttpRequest<?> createdRequest = HttpRequest.POST("/vendor-part", initPart);
        HttpResponse<VendorPart> createdResponse = client.toBlocking().exchange(createdRequest, VendorPart.class);

        HttpRequest<?> listRequest = HttpRequest.GET("/vendor-part");
        List<VendorPart> vendorParts = client.toBlocking().retrieve(listRequest, Argument.of(List.class, VendorPart.class));

        assertEquals(1, vendorParts.size());

        HttpRequest<?> listSizeRequest = HttpRequest.GET("/vendor-part?size=1");
        vendorParts = client.toBlocking().retrieve(listSizeRequest, Argument.of(List.class, VendorPart.class));

        assertEquals(1, vendorParts.size());
        assertEquals("Lemonade", vendorParts.get(0).getPartName());

        HttpRequest<?> listOrderRequest = HttpRequest.GET("/vendor-part?size=1&sort=partName,desc");
        vendorParts = client.toBlocking().retrieve(listOrderRequest, Argument.of(List.class, VendorPart.class));

        assertEquals(1, vendorParts.size());

        HttpRequest<?> listPageRequest = HttpRequest.GET("/vendor-part?size=1&page=2");
        vendorParts = client.toBlocking().retrieve(listPageRequest, Argument.of(List.class, Vendor.class));

        assertEquals(0, vendorParts.size());
    }
}
