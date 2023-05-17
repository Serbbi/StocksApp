package nl.rug.aoop.messagequeue.message;

/**
 * Defines the behaviour of a MessageQueue.
 */
public interface MessageQueue {

    /**
     * Returns the number of elements in the message queue.
     *
     * @return Number of Messages.
     */
    int getSize();

    /**
     * Removes a message from the queue.
     *
     * @return Message
     */
    Message dequeue();

    /**
     * Adds a message to the queue.
     *
     * @param message to be added to queue
     */
    void enqueue(Message message);

}
