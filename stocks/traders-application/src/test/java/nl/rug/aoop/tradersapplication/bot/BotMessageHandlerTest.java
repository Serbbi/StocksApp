package nl.rug.aoop.tradersapplication.bot;

import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

public class BotMessageHandlerTest {
    @Test
    void testHandleMessage() {
        CommandHandler commandHandler = Mockito.mock(CommandHandler.class);
        BotMessageHandler botMessageHandler = new BotMessageHandler(commandHandler);

        NetworkMessage ntm = new NetworkMessage("header", "value");
        botMessageHandler.handleMessage(ntm.toJSON());

        Mockito.verify(commandHandler).executeCommand(eq(ntm.getHeader()), argThat(x -> {
            return x.containsKey("value");
        }));
    }
}
