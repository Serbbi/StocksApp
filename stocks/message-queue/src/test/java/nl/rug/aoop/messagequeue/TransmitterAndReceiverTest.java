package nl.rug.aoop.messagequeue;

import nl.rug.aoop.messagequeue.consumer.Receiver;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.OrderedMessageQueue;
import nl.rug.aoop.messagequeue.message.UnorderedMessageQueue;
import nl.rug.aoop.messagequeue.producer.Transmitter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransmitterAndReceiverTest {

    @Test
    void testPutMessageOrderedQueue() {
        OrderedMessageQueue omq = new OrderedMessageQueue();
        Transmitter t = new Transmitter(omq);
        Message m = new Message("Send message", "This is message");
        t.put(m);

        Receiver r = new Receiver(omq);
        Message mResult = r.poll();

        assertEquals(m.getHeader(), mResult.getHeader());
        assertEquals(m.getValue(), mResult.getValue());
    }

    @Test
    void testPut2MessagesOrderedQueue() {
        OrderedMessageQueue omq = new OrderedMessageQueue();
        Transmitter t = new Transmitter(omq);

        Message m1 = new Message("Send message 1", "This is message 1");
        t.put(m1);
        Message m2 = new Message("Send message 2", "This is message 2");
        t.put(m2);

        Receiver r = new Receiver(omq);

        Message mResult = r.poll();
        assertEquals(m1.getHeader(), mResult.getHeader());
        assertEquals(m1.getValue(), mResult.getValue());

        mResult = r.poll();
        assertEquals(m2.getHeader(), mResult.getHeader());
        assertEquals(m2.getValue(), mResult.getValue());
    }

    @Test
    void testPutMessageUnorderedQueue() {
        UnorderedMessageQueue umq = new UnorderedMessageQueue();
        Transmitter t = new Transmitter(umq);
        Message m = new Message("Send message", "This is message");
        t.put(m);

        Receiver r = new Receiver(umq);
        Message mResult = r.poll();
        
        assertEquals(m.getHeader(), mResult.getHeader());
        assertEquals(m.getValue(), mResult.getValue());
    }

    @Test
    void testPut2MessagesUnorderedQueue() {
        UnorderedMessageQueue umq = new UnorderedMessageQueue();
        Transmitter t = new Transmitter(umq);

        Message m1 = new Message("Send message 1", "This is message 1");
        t.put(m1);
        Message m2 = new Message("Send message 2", "This is message 2");
        t.put(m2);

        Receiver r = new Receiver(umq);

        Message mResult = r.poll();
        assertEquals(m1.getHeader(), mResult.getHeader());
        assertEquals(m1.getValue(), mResult.getValue());

        mResult = r.poll();
        assertEquals(m2.getHeader(), mResult.getHeader());
        assertEquals(m2.getValue(), mResult.getValue());
    }
}