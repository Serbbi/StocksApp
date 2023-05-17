package nl.rug.aoop.messagequeue.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ThreadSafeOrderedMessageQueueTest {

    private ThreadSafeOrderedMessageQueue omq;

    /**
     * Sets up the global test message queue.
     */
    @BeforeEach
    public void setUp() {
        omq = new ThreadSafeOrderedMessageQueue();
    }


    /**
     * Tests that 0 is returned when the queue is empty.
     */
    @Test
    void testEmptyQueueLength() {
        assertEquals(omq.getSize(), 0);
    }

    /**
     * Tests that the message queue getSize function works as expected.
     */
    @Test
    void testQueueSize() throws InterruptedException {
        Message m1 = new Message("Message1", "The value of message 1.");

        TimeUnit.MILLISECONDS.sleep(20);

        Message m2 = new Message("Message2", "The value of message 2.");

        omq.enqueue(m1);
        omq.enqueue(m2);

        assertEquals(2, omq.getSize());
    }

    /**
     * Tests that the size function correctly counts the number of elements if there are duplicate key messages.
     */
    @Test
    void testQueueSizeWithDuplicateTimestamp() throws InterruptedException {
        Message m1 = new Message("HEADER1", "VALUE");
        TimeUnit.MILLISECONDS.sleep(20);
        Message m2 = new Message("HEADER3", "VALUE");


        omq.enqueue(m2);
        omq.enqueue(m1);
        omq.enqueue(m1);

        assertEquals(3, omq.getSize());
    }

    /**
     * Tests that IllegalArgumentException is thrown when a null value is enqueued.
     */
    @Test
    void testEnqueueNull() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> omq.enqueue(null));
    }

    /**
     * Checks that error is thrown if a message is enqueued that has a null header.
     */
    @Test
    void testEnqueueMessageWithNullHeader() {
        assertThrows(IllegalArgumentException.class, () -> omq.enqueue(new Message(null, "Value")));
    }

    /**
     * Checks that error is thrown if a message is enqueued that has a null value.
     */
    @Test
    void testEnqueueMessageWithNullValue() {
        assertThrows(IllegalArgumentException.class, () -> omq.enqueue(new Message("Header", null)));
    }

    /**
     * Tests that a null value is returned when dequeue is run on an empty list.
     */
    @Test
    void testDequeueEmpty() {
        assertEquals(null, omq.dequeue());
    }

    /**
     * Tests to ensure that a single message can be enqueued and dequeued from the queue.
     */
    @Test
    void testEnqueueDequeueSingleMessage() {
        Message m = new Message("Message1", "The value of message 1.");
        omq.enqueue(m);

        Message outputMessage = omq.dequeue();
        assertEquals(m.getHeader(), outputMessage.getHeader());
    }

    /**
     * Tests that the messages are returned in order based on their timestamp.
     */
    @Test
    void testDequeueOrder() throws InterruptedException {
        Message m1 = new Message("Message1", "The value of message 1.");

        // Added time sleep so that the timestamps are different as sometimes the test would fail.
        TimeUnit.MILLISECONDS.sleep(20);

        Message m2 = new Message("Message2", "The value of message 2.");

        omq.enqueue(m2);
        omq.enqueue(m1);

        assertNotEquals(m1.getTimestamp(), m2.getTimestamp());

        Message outputMessage = omq.dequeue();
        assertEquals(m1.getHeader(), outputMessage.getHeader());

        outputMessage = omq.dequeue();
        assertEquals(m2.getHeader(), outputMessage.getHeader());
    }

    /**
     * Tests to ensure that 2 messages with the same timestamp can be inserted into the queue.
     */
    @Test
    void testDuplicateTimestamp() {
        Message m1 = new Message("HEADER1", "VALUE");

        omq.enqueue(m1);
        omq.enqueue(m1);

        assertEquals(m1.getHeader(), omq.dequeue().getHeader());
        assertEquals(m1.getHeader(), omq.dequeue().getHeader());
    }


    /**
     * Test insertion and retrieval order.
     * Adds 2 messages with same timestamp, and one with different, and checks that all 2 come out in the expected order.
     */
    @Test
    void testRetrievalOrder() throws InterruptedException {
        Message m1 = new Message("HEADER1", "VALUE");
        TimeUnit.MILLISECONDS.sleep(20);
        Message m2 = new Message("HEADER3", "VALUE");


        omq.enqueue(m2);
        omq.enqueue(m1);
        omq.enqueue(m1);

        assertEquals(m1.getHeader(), omq.dequeue().getHeader());
        assertEquals(m1.getHeader(), omq.dequeue().getHeader());
        assertEquals(m2.getHeader(), omq.dequeue().getHeader());
    }
}