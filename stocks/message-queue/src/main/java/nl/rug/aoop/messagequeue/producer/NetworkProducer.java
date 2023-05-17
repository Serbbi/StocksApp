package nl.rug.aoop.messagequeue.producer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import nl.rug.aoop.networking.client.Client;

/**
 * Takes a message and puts it onto the message queue by sending it over the network, using the network client.
 */
public class NetworkProducer implements MQProducer {

    private final Client client;

    /**
     * Constructor that takes the network client that messages will be sent on.
     *
     * @param client Network client which messages are sent on.
     */
    public NetworkProducer(Client client) {
        this.client = client;
    }

    /**
     * Puts a message onto the message queue via the network.
     * The message to be added to the queue is put into another message which has `MqPut`
     * as the header, indicating to the server that the value is another message instance
     * which needs to be added to the message queue.
     *
     * @param message Message to be put onto the message queue.
     */
    @Override
    public void put(Message message) {
        NetworkMessage netPutMessage = new NetworkMessage("MqPut", message.toJSON());
        client.sendMessage(netPutMessage.toJSON());
    }
}
