package nl.rug.aoop.networking.client.messagehandlers;

import lombok.extern.slf4j.Slf4j;

/**
 * Class to log messages.
 * Example of handling a message.
 */
@Slf4j
public class MessageLogger implements MessageHandler {
    @Override
    public void handleMessage(String message) {
        log.info(message);
    }
}
