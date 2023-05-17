package nl.rug.aoop.tradersapplication.bot.factory;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.tradersapplication.bot.Bot;
import nl.rug.aoop.tradersapplication.bot.OrderBot;
import nl.rug.aoop.util.trader.Trader;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Creates an order bot.
 */
@Slf4j
public class OrderBotFactory implements BotFactory {

    private InetSocketAddress address;

    /**
     * Initialises the address which each bot will use to connect to the server.
     *
     * @param address Address of the server which each bot's client will connect to.
     */
    public OrderBotFactory(InetSocketAddress address) {
        this.address = address;
    }

    /**
     * Creates an individual bot for a given trader.
     *
     * @param trader Trader which the bot will use.
     * @return A bot instance.
     */
    @Override
    public Bot createBot(Trader trader) {
        try {
            return new OrderBot(address, trader);
        } catch (IOException e) {
            log.error("Failed to create bot for " + trader.getName() + ".", e);
            throw new RuntimeException(e);
        }
    }
}
