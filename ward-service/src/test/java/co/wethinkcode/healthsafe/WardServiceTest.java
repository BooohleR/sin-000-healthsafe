package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WardServiceTest {

    @Test
    void shouldReturnHealthyStatus() throws Exception {

        Javalin app = WardServiceApp.createApp().start(0);

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:" + app.port() + "/health"
                    ))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());
            assertEquals("OK", response.body());

        } finally {
            app.stop();
        }
    }
    @Test
    void shouldReturnWardsFromIngestionService() throws Exception {

        Javalin app = WardServiceApp.createApp().start(0);

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:" + app.port() + "/wards"
                    ))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode wards = mapper.readTree(response.body());

            assertEquals(17, wards.size());
            assertEquals("W-01", wards.get(0).get("wardId").asText());

        } finally {
            app.stop();
        }
    }
    @Test
    void shouldReturnUniqueDepartments() throws Exception {

        Javalin app = WardServiceApp.createApp().start(0);

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:" + app.port() + "/departments"
                    ))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode departments = mapper.readTree(response.body());

            assertEquals(6, departments.size());
            assertEquals("Cardiology", departments.get(0).asText());
            assertEquals("ICU", departments.get(1).asText());

        } finally {
            app.stop();
        }
    }
}