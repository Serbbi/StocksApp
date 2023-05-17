package nl.rug.aoop.messagequeue.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link UnorderedMessageQueueTest} Class implementation.
 */
class UnorderedMessageQueueTest {

    private UnorderedMessageQueue umq;

    /**
     * Initialises the global UnorderedMessageQueue for use in other tests.
     */
    @BeforeEach
    void setUp() {
        umq = new UnorderedMessageQueue();
    }

    /**
     * Tests that 0 is returned when the queue is empty.
     */
    @Test
    void testEmptyQueueLength() {
        assertEquals(umq.getSize(), 0);
    }

    /**
     * Tests that the message queue getSize function works as expected.
     */
    @Test
    void testQueueSize() {
        Message m1 = new Message("Message1", "The value of message 1.");
        Message m2 = new Message("Message2", "The value of message 2.");
        umq.enqueue(m1);
        umq.enqueue(m2);

        assertEquals(2, umq.getSize());
    }

    /**
     * Tests that IllegalArgumentException is thrown when a null value is enqueued.
     */
    @Test
    void testEnqueueNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> umq.enqueue(null));
        assertEquals("Invalid Message!", exception.getMessage());
    }

    /**
     * Checks that error is thrown if a message is enqueued that has a null header.
     */
    @Test
    void testEnqueueMessageWithNullHeader() {
        assertThrows(IllegalArgumentException.class, () -> umq.enqueue(new Message(null, "Value")));
    }

    /**
     * Checks that error is thrown if a message is enqueued that has a null value.
     */
    @Test
    void testEnqueueMessageWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> umq.enqueue(new Message("Header", null)));
    }

    /**
     * Tests to ensure that error is thrown if dequeue is run on an empty queue.
     */
    @Test
    void testDequeueEmpty() {
        assertThrows(NoSuchElementException.class, () -> umq.dequeue());
    }


    /**
     * Tests to ensure that a single message can be enqueued and dequeued from the queue.
     */
    @Test
    void testEnqueueDequeueSingleMessage() {
        Message m = new Message("Message1", "The value of message 1.");
        umq.enqueue(m);

        Message outputMessage = umq.dequeue();
        assertEquals(m.getHeader(), outputMessage.getHeader());
    }

    /**
     * Tests that the messages are returned in order based on the order in which they were inserted.
     */
    @Test
    void testDequeueOrder() {
        Message m1 = new Message("Message1", "The value of message 1.");
        Message m2 = new Message("Message2", "The value of message 2.");
        umq.enqueue(m1);
        umq.enqueue(m2);

        Message outputMessage = umq.dequeue();
        assertEquals(m1.getHeader(), outputMessage.getHeader());
        outputMessage = umq.dequeue();
        assertEquals(m2.getHeader(), outputMessage.getHeader());
    }

    /**
     * Tests to ensure that 2 messages with the same timestamp can be inserted into the queue.
     * Should just return the messages in order of insertion
     */
    @Test
    void testDuplicateTimestamp() {
        Message m1 = new Message("HEADER1", "VALUE");

        umq.enqueue(m1);
        umq.enqueue(m1);

        assertEquals(m1.getHeader(), umq.dequeue().getHeader());
        assertEquals(m1.getHeader(), umq.dequeue().getHeader());
    }
}