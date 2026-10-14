package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IngestionApiTest {

    @Test
    void shouldExposeCleanedWardsEndpoint() throws Exception {

        Javalin app = IngestionServiceApp.createApp().start(0);

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
            // Check that the server returns JSON
            assertTrue(
                    response.headers()
                            .firstValue("Content-Type")
                            .orElse("")
                            .contains("application/json")
            );

// Convert the JSON response into a structure we can inspect
            ObjectMapper mapper = new ObjectMapper();
            JsonNode wards = mapper.readTree(response.body());

// The response should be an array
            assertTrue(wards.isArray());

// Our CSV reader should return 17 unique wards
            assertEquals(17, wards.size());

// Check the first ward's values
            JsonNode firstWard = wards.get(0);

            assertEquals("W-01", firstWard.get("wardId").asText());
            assertEquals("East Wing", firstWard.get("wing").asText());
            assertEquals("Cardiology", firstWard.get("department").asText());
            assertEquals(3, firstWard.get("bedsAvailable").asInt());

        } finally {
            app.stop();
        }
    }
}