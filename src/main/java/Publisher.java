import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * An MQTT client publishing to topics.
 *
 * @author javiergs
 * @version 2.0
 */
public class Publisher implements PropertyChangeListener {

    private String broker;
    private String[] topics;
    private int lastX = -1;
    private int lastY = -1;
    private MqttClient client;

    public Publisher(String brokerUrl, String[] topics) {
        this.broker = brokerUrl;
        this.topics = topics;
        MqttClient client = null;

        try{
            String clientId = MqttClient.generateClientId();
            this.client = new MqttClient(broker, clientId);
            this.client.connect();
            System.out.println("↗️ Connected to broker: " + broker);
        }
        catch (MqttException e) {
            System.out.println("↗️ MQTT error: " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (lastX == Blackboard.getInstance().getMe().getX() &&
                lastY == Blackboard.getInstance().getMe().getY()) {
            return;
        }
        try {
            Player me = Blackboard.getInstance().getMe();
            String content = me.getId() + "," + me.getX() + "," + me.getY() + ","
                    + me.getColor().getRed() + "," + me.getColor().getGreen() + "," + me.getColor().getBlue();

            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(2);

            if (this.client != null && this.client.isConnected()) {
                this.client.publish(Blackboard.TOPIC, message);
                System.out.println("-- sending ->");

                lastX = me.getX();
                lastY = me.getY();
            }

        } catch (MqttException e) {
            e.printStackTrace();
        }



    }

}