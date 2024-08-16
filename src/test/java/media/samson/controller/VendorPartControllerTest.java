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
import media.samson.entity.VendorPart;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class VendorPartControllerTest {
    @Inject
    @Client("/")
    HttpClient client;

    @Test
    public void testFindNonExistingReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/vendor-part/10000000"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testCrudOperations() {
        List<BigInteger> vendorPartIds = new ArrayList<>();

        HttpRequest<?> createRequest = HttpRequest.POST("/vendor-part",
                new VendorPart("Chess Set", "Made of plastic", new BigDecimal("12.99"))
        );
        HttpResponse<VendorPart> createdResponse = client.toBlocking().exchange(createRequest, VendorPart.class);

        assertEquals(HttpStatus.CREATED, createdResponse.getStatus());
        assertFalse(createdResponse.getBody().isEmpty());

        BigInteger id = createdResponse.getBody().get().getVendorPartId();
        vendorPartIds.add(id);

        HttpRequest<?> readRequest = HttpRequest.GET("/vendor-part/" + id);
        VendorPart vendor = client.toBlocking().retrieve(readRequest, VendorPart.class);

        assertEquals("Chess Set", vendor.getPartName());
        assertEquals("Made of plastic", vendor.getPartDescription());
        assertEquals(new BigDecimal("12.99"), vendor.getPartPrice());

        HttpRequest<?> updateRequest = HttpRequest.PUT("/vendor-part",
                new VendorPart(
                        id,
                        "Plastic Chess Set",
                        "Made of plastic",
                        new BigDecimal("12.99")));
        HttpResponse<?> updateResponse = client.toBlocking().exchange(updateRequest);

        assertEquals(HttpStatus.NO_CONTENT, updateResponse.getStatus());

        HttpRequest<?> read2Request = HttpRequest.GET("/vendor-part/" + id);
        vendor = client.toBlocking().retrieve(read2Request, VendorPart.class);

        assertEquals("Plastic Chess Set", vendor.getPartName());
        assertEquals("Made of plastic", vendor.getPartDescription());

        HttpRequest<?> listRequest = HttpRequest.GET("/vendor-part");
        List<VendorPart> vendors = client.toBlocking().retrieve(listRequest, Argument.of(List.class, VendorPart.class));

        assertEquals(1, vendors.size());

        HttpRequest<?> listSizeRequest = HttpRequest.GET("/vendor-part?size=1");
        vendors = client.toBlocking().retrieve(listSizeRequest, Argument.of(List.class, VendorPart.class));

        assertEquals(1, vendors.size());
        assertEquals("Plastic Chess Set", vendors.get(0).getPartName());

        HttpRequest<?> listOrderRequest = HttpRequest.GET("/vendor-part?size=1&sort=partName,desc");
        vendors = client.toBlocking().retrieve(listOrderRequest, Argument.of(List.class, VendorPart.class));

        assertEquals(1, vendors.size());

        HttpRequest<?> listPageRequest = HttpRequest.GET("/vendor-part?size=1&page=2");
        vendors = client.toBlocking().retrieve(listPageRequest, Argument.of(List.class, VendorPart.class));

        assertEquals(0, vendors.size());

        // cleanup:
        for (BigInteger orderId : vendorPartIds) {
            HttpRequest<?> request = HttpRequest.DELETE("/vendor-part/" + orderId);
            HttpResponse<?> response = client.toBlocking().exchange(request);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        }

    }
}
