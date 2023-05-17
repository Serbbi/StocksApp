package nl.rug.aoop.messagequeue.commands;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.MessageQueue;

import java.util.Map;

/**
 * Command that adds a message to the a given message queue.
 */
@Slf4j
public class MqPutCommand implements Command {
    private final MessageQueue mq;

    /**
     * Initialises the MessageQueue that messages will be added to.
     *
     * @param mq MessageQueue.
     */
    public MqPutCommand(MessageQueue mq) {
        this.mq = mq;
    }

    /**
     * Adds a message to the queue. The message is stored in the options map as a JSON string,
     * so must first be converted back to a Message instance before being added to the queue.
     *
     * @param options Map that holds extra data for the command.
     */
    @Override
    public void execute(Map<String, Object> options) {
        String jsonMessage = (String) options.get("value");
        Message message = Message.fromJSON(jsonMessage);

        mq.enqueue(message);
        log.info("Enqueued message: " + jsonMessage);
    }
}
