package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlertLevelServiceTest {

    @Test
    void shouldReturnInitialAlertLevel() throws Exception {

        Javalin app = AlertLevelServiceApp.createApp().start(0);

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:" + app.port()
                                    + "/alert-level"
                    ))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());
            assertEquals("{\"level\":0}", response.body());

        } finally {
            app.stop();
        }
    }
    @Test
    void shouldUpdateAlertLevel() throws Exception {

        Javalin app = AlertLevelServiceApp.createApp().start(0);

        try {
            HttpClient client = HttpClient.newHttpClient();

            String url = "http://localhost:" + app.port() + "/alert-level";

            HttpRequest updateRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .PUT(HttpRequest.BodyPublishers.ofString("level=5"))
                    .build();

            HttpResponse<String> updateResponse = client.send(
                    updateRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, updateResponse.statusCode());
            assertEquals("{\"level\":5}", updateResponse.body());

            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> getResponse = client.send(
                    getRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals("{\"level\":5}", getResponse.body());

        } finally {
            app.stop();
        }
    }

    @Test
    void shouldRejectAlertLevelAboveEight() throws Exception {

        Javalin app = AlertLevelServiceApp.createApp().start(0);

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:" + app.port() + "/alert-level"
                    ))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .PUT(HttpRequest.BodyPublishers.ofString("level=10"))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(400, response.statusCode());

        } finally {
            app.stop();
        }
    }

    @Test
    void shouldRejectNegativeAlertLevel() throws Exception {

        Javalin app = AlertLevelServiceApp.createApp().start(0);

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:" + app.port() + "/alert-level"
                    ))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .PUT(HttpRequest.BodyPublishers.ofString("level=-1"))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(400, response.statusCode());

        } finally {
            app.stop();
        }
    }
}