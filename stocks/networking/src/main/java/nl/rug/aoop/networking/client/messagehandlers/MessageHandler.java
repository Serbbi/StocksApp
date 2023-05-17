package nl.rug.aoop.networking.client.messagehandlers;

/**
 * Interface to handle messages in different ways.
 */
public interface MessageHandler {
    /**
     * Method to manipulate a message in some way.
     *
     * @param message the string to be manipulated.
     */
    void handleMessage(String message);
}
