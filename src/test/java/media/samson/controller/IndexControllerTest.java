package media.samson.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class IndexControllerTest {
    @Inject
    @Client("/")
    HttpClient client;

    @Test
    public void testIndex() {
        var response = client.toBlocking().exchange(HttpRequest.GET("/"));
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}
