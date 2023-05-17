package nl.rug.aoop.networking.server;

import java.util.UUID;

/**
 * Used to link a client handler with another identifier which is used to send responses to the client.
 */
public interface ClientLoginHandler {

    /**
     * Handles the login.
     *
     * @param clientHandlerID The client handler id which can be used to send a response.
     * @param identifier      THe identifier for the client.
     */
    void login(UUID clientHandlerID, String identifier);
}
