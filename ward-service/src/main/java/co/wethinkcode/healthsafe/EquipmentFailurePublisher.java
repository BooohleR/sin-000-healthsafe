package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.mq.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

public class EquipmentFailurePublisher {

    public static void publish(String failureJson) throws Exception {

        ActiveMQConnectionFactory factory =
                new ActiveMQConnectionFactory(MqConfig.BROKER_URL);

        try (Connection connection = factory.createConnection()) {
            connection.start();

            Session session = connection.createSession(
                    false,
                    Session.AUTO_ACKNOWLEDGE
            );

            try {
                Queue queue = session.createQueue(MqConfig.QUEUE);
                MessageProducer producer = session.createProducer(queue);

                try {
                    producer.setDeliveryMode(DeliveryMode.PERSISTENT);

                    TextMessage message =
                            session.createTextMessage(failureJson);

                    producer.send(message);

                    System.out.println(
                            "Published equipment failure: " + failureJson
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