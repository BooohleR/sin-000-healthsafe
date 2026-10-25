package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

public class StaffingEventPublisher {

    public static void publish(String eventJson) throws Exception {

        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        try (Connection connection = factory.createConnection()) {

            connection.start();

            Session session = connection.createSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE
            );

            try {
                Topic topic = session.createTopic(MqConfig.TOPIC);

                MessageProducer producer = session.createProducer(topic);

                try {
                    TextMessage message = session.createTextMessage(eventJson);
                    producer.send(message);

                    System.out.println(
                            "Published staffing event: " + eventJson
                    );
                } finally {
                    producer.close();
                }
            } finally {
                session.close();
            }
        }
    }
}