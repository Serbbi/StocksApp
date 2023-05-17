package nl.rug.aoop.messagequeue.consumer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.OrderedMessageQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReceiverTest {

    /**
     * Test if the polled message is polled successfully.
     */
    @Test
    void testPollMessage() {
        OrderedMessageQueue omq = new OrderedMessageQueue();

        Message message = new Message("TEST", "testValue");

        omq.enqueue(message);
        Message polledMessage = omq.dequeue();
        assertEquals(message,polledMessage);
    }
}
