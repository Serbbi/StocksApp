package nl.rug.aoop.messagequeue.message;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link Message} Class implementation.
 */
@Slf4j
class MessageTest {
    private final String message_header = "NEWHEADER";
    private final String message_value = "This is a new message";

    /**
     * Test that the constructor performs generates a message object properly.
     * The constructor should generate the timestamp, and set the key and value to the values passed in.
     */
    @Test
    void testConstructor() {
        Message message = new Message(this.message_header, this.message_value);

        assertNotNull(message.getTimestamp());
        assertEquals(message.getValue(), message_value);
        assertEquals(message.getHeader(), message_header);
    }

    /**
     * Tests converting message to json.
     */
    @Test
    void testConvertJSON() {
        Message message = new Message(this.message_header, this.message_value);
        Gson gson = Message.getGson();
        assertEquals(gson.toJson(message), message.toJSON());
    }

    /**
     * Tests that a Message object can be produced from a JSON string.
     */
    @Test
    void testProduceMessageFromJSON() {
        Message message = new Message(this.message_header, this.message_value);
        Gson gson = Message.getGson();
        String messageJSON = gson.toJson(message);

        Message fromJsonMessage = Message.fromJSON(messageJSON);
        assertEquals(message.getHeader(), fromJsonMessage.getHeader());
        assertEquals(message.getValue(), fromJsonMessage.getValue());
    }

}