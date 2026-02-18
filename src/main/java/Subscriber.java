import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * An MQTT client subscribing to topics.
 *
 * @author javiergs
 * @version 2.0
 */
public class Subscriber implements MqttCallback {

    private String broker;
    private String[] topics;

    public Subscriber(String brokerUrl, String[] topics) {
        this.broker = brokerUrl;
        this.topics = topics;
        MqttClient client = null;

        try {
            String clientId = MqttClient.generateClientId();
            client = new MqttClient(broker, clientId);
            client.setCallback(this);
            client.connect();
            client.subscribe(Blackboard.TOPIC);

        }catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage message) {
        String payload = new String(message.getPayload());
        System.out.println("↙️ Received: " + payload);
        Blackboard.getInstance().addPlayerFromPayload(payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void connectionLost(Throwable throwable) {
    }
}