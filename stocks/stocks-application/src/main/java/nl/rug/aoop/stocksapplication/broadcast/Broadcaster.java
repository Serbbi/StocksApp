package nl.rug.aoop.stocksapplication.broadcast;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import nl.rug.aoop.networking.server.Server;
import nl.rug.aoop.util.BroadcastData;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.Trader;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;

/**
 * Broadcaster class is used to send messages to traders from the stock application.
 */
@Slf4j
public class Broadcaster implements Runnable {
    @Setter
    private Server server;
    @Setter
    private Map<String, Stock> stocksMap;
    @Setter
    private Map<String, Trader> tradersMap;
    @Setter
    @Getter
    private Map<String, UUID> clientHandlerTradersMap;

    /**
     * Constructor for the broadcaster.
     *
     * @param server                  server from the stockapp.
     * @param stocksMap               map for the stock.
     * @param tradersMap              map for the traders.
     * @param clientHandlerTradersMap map for the clienthandlers.
     */
    public Broadcaster(Server server, Map stocksMap, Map tradersMap, Map clientHandlerTradersMap) {
        this.server = server;
        this.stocksMap = stocksMap;
        this.tradersMap = tradersMap;
        this.clientHandlerTradersMap = clientHandlerTradersMap;
    }

    /**
     * Default constructor.
     */
    public Broadcaster() {
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            sendBroadcast();
        }
    }

    /**
     * Method to send info to all traders.
     */
    public void sendBroadcast() {
        if (clientHandlerTradersMap.size() > 0) {
            log.info("Sending broadcast!");
        }
        clientHandlerTradersMap.forEach((traderID, uuid) -> {
            server.sendResponse(uuid, getJsonBroadcastMessage(tradersMap.get(traderID)));
        });
    }

    /**
     * Sends a broadcast to an individual client.
     *
     * @param traderID Trader to send the broadcast to.
     */
    public void sendBroadcast(String traderID) {
        UUID uuid = clientHandlerTradersMap.get(traderID);
        server.sendResponse(uuid, getJsonBroadcastMessage(tradersMap.get(traderID)));
    }

    /**
     * Method to send a notification(custom) message to a trader.
     *
     * @param traderID the trader to be notified.
     * @param message  the message to be sent.
     */
    public void sendCustomMessage(String traderID, String message) {
        server.sendResponse(clientHandlerTradersMap.get(traderID),
                new NetworkMessage("notification", message).toJSON());
        log.info(format("(%s) Sent notification to: %s", traderID, message));
    }

    /**
     * Gets the json data to be sent.
     *
     * @param trader the trader info.
     * @return String json data.
     */
    public String getJsonBroadcastMessage(Trader trader) {
        BroadcastData broadcastData = new BroadcastData(stocksMap, trader);
        NetworkMessage response = new NetworkMessage("broadcast", broadcastData.toJson());
        return response.toJSON();
    }

    /**
     * Sets all the fields for the broadcaster.
     *
     * @param stocksMap               stocks map.
     * @param tradersMap              traders map.
     * @param server                  server.
     * @param clientHandlerTradersMap clienthandler traders map.
     */
    public void setAll(Map<String, Stock> stocksMap, Map<String, Trader> tradersMap, Server server,
                       Map<String, UUID> clientHandlerTradersMap) {
        setStocksMap(stocksMap);
        setTradersMap(tradersMap);
        setServer(server);
        setClientHandlerTradersMap(clientHandlerTradersMap);
    }
}
