package co.wethinkcode.healthsafe;

import io.javalin.Javalin;

public class EquipmentAlertServiceApp {

    public static void main(String[] args) throws Exception {

        EquipmentFailureConsumer consumer =
                new EquipmentFailureConsumer();

        consumer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                consumer.stop();
            } catch (Exception e) {
                System.err.println(
                        "Error stopping equipment consumer: "
                                + e.getMessage()
                );
            }
        }));

        Javalin app = Javalin.create();

        app.get("/health", ctx -> ctx.result("OK"));

        app.start(7034);
    }
}