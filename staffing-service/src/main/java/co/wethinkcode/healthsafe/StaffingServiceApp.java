
package co.wethinkcode.healthsafe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class StaffingServiceApp {

    private static final String WARD_SERVICE_URL =
            "http://localhost:7031/wards/";

    private static final String ALERT_SERVICE_URL =
            "http://localhost:7032/alert-level";

    public static void main(String[] args) {
        createApp().start(7033);
    }

    public static Javalin createApp() {

        Javalin app = Javalin.create();

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/staffing/{wardId}", ctx -> {

            String wardId = ctx.pathParam("wardId");

            // Step 1: Validate the ward using ward-service
            HttpRequest wardRequest = HttpRequest.newBuilder()
                    .uri(URI.create(WARD_SERVICE_URL + wardId))
                    .GET()
                    .build();

            HttpResponse<String> wardResponse = client.send(
                    wardRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (wardResponse.statusCode() == 404) {
                ctx.status(404).result("Ward not found");
                return;
            }

            if (wardResponse.statusCode() != 200) {
                ctx.status(502).result("Ward service unavailable");
                return;
            }

            JsonNode ward = mapper.readTree(wardResponse.body());

            // Step 2: Retrieve current emergency level
            HttpRequest alertRequest = HttpRequest.newBuilder()
                    .uri(URI.create(ALERT_SERVICE_URL))
                    .GET()
                    .build();

            HttpResponse<String> alertResponse = client.send(
                    alertRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (alertResponse.statusCode() != 200) {
                ctx.status(502).result("Alert service unavailable");
                return;
            }

            JsonNode alert = mapper.readTree(alertResponse.body());
            int level = alert.path("level").asInt();

            // Step 3: Calculate the required doctors
            int doctorsRequired;
            String status;

            if (level <= 2) {
                doctorsRequired = 1;
                status = "NORMAL";
            } else if (level <= 5) {
                doctorsRequired = 2;
                status = "HIGH";
            } else if (level <= 7) {
                doctorsRequired = 3;
                status = "CRITICAL";
            } else {
                doctorsRequired = 4;
                status = "CODE BLUE";
            }

            // Step 4: Return the schedule summary
            ctx.json(Map.of(
                    "wardId", ward.path("wardId").asText(),
                    "department", ward.path("department").asText(),
                    "emergencyLevel", level,
                    "doctorsRequired", doctorsRequired,
                    "status", status
            ));
        });

        return app;
    }
}
