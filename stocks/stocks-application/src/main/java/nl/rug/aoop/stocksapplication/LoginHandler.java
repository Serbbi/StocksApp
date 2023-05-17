package nl.rug.aoop.stocksapplication;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.server.ClientLoginHandler;

import java.util.Map;
import java.util.UUID;

/**
 * LogInHandler maps a clientHandlerUUID to a traderID.
 */
@Slf4j
public class LoginHandler implements ClientLoginHandler {
    private final Map<String, UUID> clientHandlerTradersMap;

    /**
     * Constructor for the handler.
     *
     * @param clientHandlerTradersMap the map between the cliendhandler id and the trader id.
     */
    public LoginHandler(Map<String, UUID> clientHandlerTradersMap) {
        this.clientHandlerTradersMap = clientHandlerTradersMap;
    }

    @Override
    public void login(UUID clientHandlerID, String identifier) {
        log.info("Handling login of " + identifier);
        clientHandlerTradersMap.put(identifier, clientHandlerID);
    }

}
