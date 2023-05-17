package nl.rug.aoop.messagequeue.message;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Keeps the messages in order based on their timestamp, allowing them to be retrieved in the order they were created.
 */
public class OrderedMessageQueue implements MessageQueue {
    private final NavigableMap<LocalDateTime, LinkedList<Message>> map;

    /**
     * Initialises the map.
     */
    public OrderedMessageQueue() {
        map = new TreeMap<>();
    }

    /**
     * Used to get the size of the tree map.
     * Uses a loop to check how many elements are in the linked lists at each key position.
     *
     * @return Number of elements in the Tree map.
     */
    @Override
    public int getSize() {

        Integer size = 0;
        for (LinkedList<Message> value : map.values()) {
            size += value.size();
        }
        return size;
    }

    /**
     * Removes the first message from the map.
     * IF there are multiple messages with same timestamp, then they will behave like unordered list,
     * so the first message that was received will be returned first.
     * If the linked list at a position in the map has only one item then that key is removed from the map.
     * If there are still items in the linked list at the key position, then it is left in the map,
     * but the message is removed from the linked list.
     *
     * @return message with the lowest timestamp.
     * @throws NoSuchElementException Thrown when no elements in queue.
     */
    @Override
    public Message dequeue() {
        NavigableMap.Entry<LocalDateTime, LinkedList<Message>> entry = map.firstEntry();
        if (entry != null && entry.getValue().size() > 0) {

            Message value = entry.getValue().poll();

            if (entry.getValue().size() == 0) {
                map.remove(value.getTimestamp());
            }

            return value;
        }

        throw new NoSuchElementException("Queue is empty!");
    }

    /**
     * Adds a message to the map.
     * Each message is first added to a linked list, to allow for multiple messages with the same timestamp
     * to be added to the queue.
     * If a timestamp is already in the list, then the message is added to the end of the linked list at
     * the timestamp in the map.
     *
     * @param message to be added to the map.
     * @throws IllegalArgumentException when message is null.
     */
    @Override
    public void enqueue(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Invalid Message");
        }

        if (map.containsKey(message.getTimestamp())) {
            map.get(message.getTimestamp()).addLast(message);
            // Add message to linked list at key location.
        } else {
            // Add new linked list to map
            LinkedList<Message> subQueue = new LinkedList<>();
            subQueue.addLast(message);
            map.put(message.getTimestamp(), subQueue);
        }
    }

}
