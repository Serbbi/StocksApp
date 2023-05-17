package nl.rug.aoop.tradersapplication.bot.factory;

import nl.rug.aoop.tradersapplication.bot.Bot;
import nl.rug.aoop.util.trader.Trader;

/**
 * Factory to create bots.
 */
public interface BotFactory {

    /**
     * Creates a bot.
     *
     * @param trader Trader which this bot will be.
     * @return A bot.
     */
    Bot createBot(Trader trader);

}
