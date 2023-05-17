package nl.rug.aoop.messagequeue.message;

import lombok.extern.slf4j.Slf4j;

import java.util.AbstractQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * A Thread Safe Implementation of {@link MessageQueue}, that provides the same functionality
 * as {@link OrderedMessageQueue}.
 */
@Slf4j
public class ThreadSafeOrderedMessageQueue implements MessageQueue {

    private final AbstractQueue<Message> messageQueue;

    /**
     * Constructor for MessageQueue implementation.
     * Uses comparator to ensure that the messages are ordered by their timestamp in the queue.
     */
    public ThreadSafeOrderedMessageQueue() {
        this.messageQueue = new PriorityBlockingQueue<>(10, new MessageTimeStampComparator());
    }

    /**
     * Get the number of elements in the queue.
     *
     * @return Number of elements in the queue.
     */
    @Override
    public int getSize() {
        return messageQueue.size();
    }

    /**
     * Removes the highest priority element from the queue.
     * This would be the message with the earliest timestamp.
     *
     * @return Message with the earliest timestamp.
     */
    @Override
    public Message dequeue() {
        if (this.getSize() < 1) {
            return null;
        }
        return messageQueue.poll();
    }

    /**
     * Adds a message to the queue.
     *
     * @param message to be added to queue
     */
    @Override
    public void enqueue(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Invalid Message");
        }
        log.info("Added message to the queue!");
        messageQueue.add(message);
    }
}
