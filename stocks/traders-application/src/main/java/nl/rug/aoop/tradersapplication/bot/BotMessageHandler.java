package nl.rug.aoop.tradersapplication.bot;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import nl.rug.aoop.networking.client.messagehandlers.MessageHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles messages received from the server.
 */
@Slf4j
public class BotMessageHandler implements MessageHandler {
    private final CommandHandler commandHandler;

    /**
     * Sets the commandHandler.
     *
     * @param commandHandler Command handler to be used to handle the messages.
     */
    public BotMessageHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handleMessage(String message) {
        NetworkMessage msg = NetworkMessage.fromJSON(message);
        Map<String, Object> options = new HashMap<>();
        options.put("value", msg.getValue());
        commandHandler.executeCommand(msg.getHeader(), options);
    }
}
