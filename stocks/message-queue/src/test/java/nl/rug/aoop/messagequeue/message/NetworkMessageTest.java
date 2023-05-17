package nl.rug.aoop.messagequeue.message;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NetworkMessageTest {

    private final String message_header = "NEWHEADER";
    private final String message_value = "This is a new message";

    /**
     * Tests converting message to json.
     */
    @Test
    void testConvertJSON() {
        NetworkMessage message = new NetworkMessage(this.message_header, this.message_value);
        Gson gson = new Gson();
        assertEquals(gson.toJson(message), message.toJSON());
    }

    /**
     * Tests that a Message object can be produced from a JSON string.
     */
    @Test
    void testProduceMessageFromJSON() {
        NetworkMessage message = new NetworkMessage(this.message_header, this.message_value);
        Gson gson = new Gson();
        String messageJSON = gson.toJson(message);

        NetworkMessage fromJsonMessage = NetworkMessage.fromJSON(messageJSON);
        assertEquals(message.getHeader(), fromJsonMessage.getHeader());
        assertEquals(message.getValue(), fromJsonMessage.getValue());
    }
}
