package nl.rug.aoop.messagequeue.producer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import nl.rug.aoop.networking.client.Client;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class NetworkProducerTest {


    @Test
    void testPutMessage() {
        Client client = Mockito.mock(Client.class);

        NetworkProducer np = new NetworkProducer(client);

        Message message = new Message("Test Message", "Message!");

        np.put(message);

        NetworkMessage mqPutMessage = new NetworkMessage("MqPut", message.toJSON());

        Mockito.verify(client).sendMessage(mqPutMessage.toJSON());
    }
}