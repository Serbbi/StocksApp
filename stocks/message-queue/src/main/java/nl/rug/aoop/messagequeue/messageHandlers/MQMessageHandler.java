package nl.rug.aoop.messagequeue.messageHandlers;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import nl.rug.aoop.networking.client.messagehandlers.MessageHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all messages received from the server and passes them as commands to the commandHandler.
 */
@Slf4j
public class MQMessageHandler implements MessageHandler {

    private final CommandHandler commandHandler;

    /**
     * Stores the command handler that will be used to process the commands.
     *
     * @param commandHandler Will handle each message's command.
     */
    public MQMessageHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handleMessage(String jsonMessage) {
        NetworkMessage message = NetworkMessage.fromJSON(jsonMessage);

        Map<String, Object> map = new HashMap<>();
        map.put("value", message.getValue());
        log.info("Received Message: " + map.get("value"));
        log.info("MqMessageHandler is executing command " + message.getHeader());

        commandHandler.executeCommand(message.getHeader(), map);
    }
}
