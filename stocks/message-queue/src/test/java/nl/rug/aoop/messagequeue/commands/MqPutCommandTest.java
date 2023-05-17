package nl.rug.aoop.messagequeue.commands;

import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.MessageQueue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

class MqPutCommandTest {

    /**
     * Verifies that the mqPutCommand adds a message to a message queue.
     */
    @Test
    void testExecute() {
        MessageQueue mq = Mockito.mock(MessageQueue.class);
        MqPutCommand mqPutCommand = new MqPutCommand(mq);

        Message message = new Message("New Message", "Body of the new message.");

        Map<String, Object> map = new HashMap<>();
        map.put("value", message.toJSON());
        map.put("timestamp", message.getTimestamp());

        mqPutCommand.execute(map);

        Mockito.verify(mq).enqueue(any(Message.class));
    }
}