package nl.rug.aoop.messagequeue.messageHandlers;

import com.google.gson.Gson;
import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.messagequeue.message.Message;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

class MQMessageHandlerTest {

    @Test
    void handleMessageTest() {
        CommandHandler commandHandler = Mockito.mock(CommandHandler.class);
        MQMessageHandler messageHandler = new MQMessageHandler(commandHandler);

        Message message = new Message("Message", "Message Body!");

        Gson gson = new Gson();

        messageHandler.handleMessage(gson.toJson(message));
        ArgumentCaptor<Map> map = ArgumentCaptor.forClass(Map.class);
        // Checks that all values were added to the map.
        Mockito.verify(commandHandler).executeCommand(eq(message.getHeader()), map.capture());

        assertEquals(message.getValue(),map.getValue().get("value"));
    }
}