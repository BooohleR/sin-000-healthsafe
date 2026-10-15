package co.wethinkcode.healthsafe;

import io.javalin.Javalin;
import java.util.List;

public class IngestionServiceApp {

    public static void main(String[] args) throws Exception {
        Javalin app = createApp();
        app.start(7030);
    }

    public static Javalin createApp() throws Exception {

        Javalin app = Javalin.create();

        WardCsvReader reader = new WardCsvReader();

        List<WardRecord> wards =
                reader.readFromResources("wards-outdated.csv");

        app.get("/health", ctx -> ctx.result("OK"));

        app.get("/wards", ctx -> ctx.json(wards));

        return app;
    }
}