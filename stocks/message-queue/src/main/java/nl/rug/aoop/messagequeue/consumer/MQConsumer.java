package nl.rug.aoop.messagequeue.consumer;

import nl.rug.aoop.messagequeue.message.Message;

/**
 * Dictates the methods that a class that will receive messages will have.
 */
public interface MQConsumer {

    /**
     * Poll method to get messages from a queue.
     *
     * @return message removed from queue.
     */
    Message poll();
}
