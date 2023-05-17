package nl.rug.aoop.messagequeue.producer;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.MessageQueue;

/**
 * Transmitter class to act as a Producer.
 */
public class Transmitter implements MQProducer {

    private final MessageQueue messageQueue;

    /**
     * Transmitter constructor.
     *
     * @param messageQueue the queue where the producer puts the messages.
     */
    public Transmitter(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    /**
     * Puts a message onto the message queue.
     *
     * @param message Message to be added to the message queue.
     */
    @Override
    public void put(Message message) {
        messageQueue.enqueue(message);
    }

}
