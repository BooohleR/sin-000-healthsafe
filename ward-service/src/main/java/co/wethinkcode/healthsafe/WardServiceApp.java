package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.TreeSet;

public class WardServiceApp {

    private static final String INGESTION_URL =
            "http://localhost:7030/wards";

    public static void main(String[] args) throws Exception {

        StaffingEventSubscriber subscriber = new StaffingEventSubscriber();

        subscriber.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                subscriber.stop();
            } catch (Exception e) {
                System.err.println("Error stopping subscriber: " + e.getMessage());
            }
        }));

        createApp().start(7031);
    }

    public static Javalin createApp() {

        Javalin app = Javalin.create();

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        app.get("/health", ctx -> ctx.result("OK"));

        // Retrieve cleaned ward records from ingestion-service
        app.get("/wards", ctx -> {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(INGESTION_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                ctx.status(502).result("Ingestion service unavailable");
                return;
            }

            ctx.contentType("application/json");
            ctx.result(response.body());
        });

        // Retrieve unique departments from ingestion-service
        app.get("/departments", ctx -> {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(INGESTION_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                ctx.status(502).result("Ingestion service unavailable");
                return;
            }

            JsonNode wards = mapper.readTree(response.body());

            Set<String> departments = new TreeSet<>();

            for (JsonNode ward : wards) {
                JsonNode department = ward.get("department");

                if (department != null && !department.isNull()) {
                    departments.add(department.asText());
                }
            }

            ctx.json(departments);
        });
        app.get("/wards/{id}", ctx -> {

            String wardId = ctx.pathParam("id");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(INGESTION_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != 200) {
                ctx.status(502).result("Ingestion service unavailable");
                return;
            }

            JsonNode wards = mapper.readTree(response.body());

            for (JsonNode ward : wards) {

                if (ward.path("wardId").asText()
                        .equalsIgnoreCase(wardId)) {

                    ctx.json(ward);
                    return;
                }
            }

            ctx.status(404).result("Ward not found");
        });

        // Report an equipment failure and publish it to ActiveMQ
        app.post("/equipment-failure", ctx -> {

            JsonNode failure;

            try {
                failure = mapper.readTree(ctx.body());
            } catch (Exception e) {
                ctx.status(400).result("Invalid JSON");
                return;
            }

            String wardId = failure.path("wardId").asText("").trim();
            String equipment = failure.path("equipment").asText("").trim();
            String description = failure.path("description").asText("").trim();

            if (wardId.isEmpty() || equipment.isEmpty() || description.isEmpty()) {
                ctx.status(400).result(
                        "wardId, equipment and description are required"
                );
                return;
            }

            String eventJson = mapper.writeValueAsString(
                    java.util.Map.of(
                            "wardId", wardId,
                            "equipment", equipment,
                            "description", description
                    )
            );

            try {
                EquipmentFailurePublisher.publish(eventJson);
            } catch (Exception e) {
                System.err.println(
                        "Equipment queue unavailable: " + e.getMessage()
                );
                ctx.status(503).result("Equipment messaging unavailable");
                return;
            }

            ctx.status(202).json(
                    java.util.Map.of(
                            "message", "Equipment failure queued",
                            "wardId", wardId
                    )
            );
        });

        return app;
    }
}