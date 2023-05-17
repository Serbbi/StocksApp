package nl.rug.aoop.messagequeue.producer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.OrderedMessageQueue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransmitterTest {

    /**
     * Test if the messages is put in the queue.
     */
    @Test
    void testPutMessageOnQueue() {
        OrderedMessageQueue omq = new OrderedMessageQueue();

        Message message = new Message("TEST", "testValue");

        omq.enqueue(message);
        assertEquals(omq.getSize(),1);
    }
}
