package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class EquipmentFailureConsumer {

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    public void start() throws Exception {

        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        connection = factory.createConnection();

        session = connection.createSession(
                true,
                Session.SESSION_TRANSACTED
        );

        Queue queue = session.createQueue(MqConfig.QUEUE);

        consumer = session.createConsumer(queue);

        consumer.setMessageListener(message -> {
            try {
                if (!(message instanceof TextMessage)) {
                    throw new IllegalArgumentException(
                            "Unsupported equipment message type"
                    );
                }

                String failureJson = ((TextMessage) message).getText();

                // Demonstration of processing the equipment alert
                System.out.println(
                        "Equipment alert received: " + failureJson
                );

                // Acknowledge only after successful processing
                session.commit();

            } catch (Exception e) {
                System.err.println(
                        "Equipment alert processing failed: " + e.getMessage()
                );

                try {
                    session.rollback();
                } catch (Exception rollbackError) {
                    System.err.println(
                            "Rollback failed: " + rollbackError.getMessage()
                    );
                }
            }
        });

        connection.start();

        System.out.println(
                "Listening for equipment failures on: " + MqConfig.QUEUE
        );
    }

    public void stop() throws Exception {
        if (consumer != null) consumer.close();
        if (session != null) session.close();
        if (connection != null) connection.close();
    }
}