package nl.rug.aoop.messagequeue.consumer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.MessageQueue;

/**
 * Receiver class to act as a Consumer.
 */
public class Receiver implements MQConsumer {

    private final MessageQueue messageQueue;

    /**
     * Constructor for the receiver.
     *
     * @param messageQueue takes as argument a message queue to get messages.
     */
    public Receiver(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    /**
     * Gets the next message off the message queue.
     *
     * @return The message the was removed from the queue.
     */
    @Override
    public Message poll() {
        return messageQueue.dequeue();
    }
}
