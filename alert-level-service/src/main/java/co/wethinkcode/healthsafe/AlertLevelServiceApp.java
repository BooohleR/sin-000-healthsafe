package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AlertLevelServiceApp {

    public static void main(String[] args) {
        createApp().start(7032);
    }

    public static Javalin createApp() {

        Javalin app = Javalin.create();

        AtomicInteger alertLevel = new AtomicInteger(0);

        // Health endpoint
        app.get("/health", ctx -> ctx.result("OK"));

        // Get the current alert level
        app.get("/alert-level", ctx -> {
            ctx.json(Map.of("level", alertLevel.get()));
        });

        // Update the alert level
        app.put("/alert-level", ctx -> {

            try {
                int newLevel = Integer.parseInt(
                        ctx.formParam("level")
                );

                if (newLevel < 0 || newLevel > 8) {
                    ctx.status(400).result(
                            "Alert level must be between 0 and 8"
                    );
                    return;
                }

                alertLevel.set(newLevel);

                ctx.json(Map.of("level", alertLevel.get()));

            } catch (NumberFormatException e) {
                ctx.status(400).result(
                        "Invalid alert level"
                );
            }
        });

        return app;
    }
}