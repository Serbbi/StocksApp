package nl.rug.aoop.messagequeue.message;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Behaves like a regular queue. Stores the Messages in order based on when they arrived.
 * Uses a FIFO structure.
 * Uses the least specific type for queue.
 */
public class UnorderedMessageQueue implements MessageQueue {

    private final Queue<Message> queue;

    /**
     * Initialises the linked list that will be used to store the queue data.
     */
    public UnorderedMessageQueue() {
        queue = new LinkedList<>();
    }

    /**
     * Returns the number of messages in the queue.
     *
     * @return Number of messages.
     */
    @Override
    public int getSize() {
        return queue.size();
    }

    /**
     * Removes the first item from the queue.
     *
     * @return The first item in the queue.
     * @throws NoSuchElementException when queue is empty.
     */
    @Override
    public Message dequeue() {
        if (queue.isEmpty()) {
            throw new NoSuchElementException("Queue Empty!");
        }
        return queue.poll();
    }

    /**
     * Adds a message to the queue.
     *
     * @param message to be added to queue.
     * @throws IllegalArgumentException if message is null.
     */
    @Override
    public void enqueue(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Invalid Message!");
        }
        queue.add(message);
    }
}
