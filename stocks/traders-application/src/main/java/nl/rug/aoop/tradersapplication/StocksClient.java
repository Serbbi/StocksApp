package nl.rug.aoop.tradersapplication;

import nl.rug.aoop.networking.client.Client;
import nl.rug.aoop.util.trader.Trader;

/**
 * Interface for all objects that behave as a client that connects to the stocks server,
 * and interacts with it as a trader.
 */
public interface StocksClient extends Runnable {

    /**
     * Sends the login message with their id to the server.
     */
    void sendLoginMessage();

    /**
     * Stops the bot.
     */
    void terminate();

    /**
     * Gets the trader that the stocks client is for.
     *
     * @return Trader.
     */
    Trader getTrader();

    /**
     * Gets the network client of the stocks client.
     *
     * @return Network Client.
     */
    Client getClient();

    /**
     * Gets if the stocks client is running.
     *
     * @return IsRunning.
     */
    Boolean getRunning();
}
