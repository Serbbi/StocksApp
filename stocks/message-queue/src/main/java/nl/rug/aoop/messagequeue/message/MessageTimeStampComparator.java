package nl.rug.aoop.messagequeue.message;

import java.util.Comparator;

/**
 * Used for message priority queues. Prioritise messages with earlier timestamps.
 */
public class MessageTimeStampComparator implements Comparator<Message> {
    @Override
    public int compare(Message m1, Message m2) {
        if (m1.getTimestamp().isBefore(m2.getTimestamp())) {
            return -1;
        } else if (m1.getTimestamp().isEqual(m2.getTimestamp())) {
            return 0;
        }
        return 1;
    }
}
