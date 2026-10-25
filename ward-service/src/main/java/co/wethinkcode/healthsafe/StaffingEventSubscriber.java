package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;

public class StaffingEventSubscriber {

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    public void start() throws Exception {

        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        connection = factory.createConnection();

        session = connection.createSession(
                false,
                Session.AUTO_ACKNOWLEDGE
        );

        Topic topic = session.createTopic(MqConfig.TOPIC);

        consumer = session.createConsumer(topic);

        consumer.setMessageListener(message -> {
            try {
                if (message instanceof javax.jms.TextMessage) {
                    String eventJson =
                            ((javax.jms.TextMessage) message).getText();

                    System.out.println(
                            "Ward service received staffing event: " + eventJson
                    );
                }
            } catch (Exception e) {
                System.err.println(
                        "Error processing staffing event: " + e.getMessage()
                );
            }
        });

        connection.start();

        System.out.println(
                "Ward service subscribed to: " + MqConfig.TOPIC
        );
    }

    public void stop() throws Exception {
        if (consumer != null) consumer.close();
        if (session != null) session.close();
        if (connection != null) connection.close();
    }
}