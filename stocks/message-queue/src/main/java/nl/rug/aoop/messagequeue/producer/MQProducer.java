package nl.rug.aoop.messagequeue.producer;

import nl.rug.aoop.messagequeue.message.Message;

/**
 * Dictates the methods that a class that produces messages will have.
 */
public interface MQProducer {
    /**
     * Put method to put messages in the queue.
     *
     * @param message Message to be put onto the message queue.
     */
    void put(Message message);
}
