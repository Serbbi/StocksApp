package nl.rug.aoop.tradersapplication.command;

import nl.rug.aoop.command.CommandHandlerBuilder;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.Trader;

import java.util.Map;

/**
 * Used to build a command handler for a stocks client.
 */
public class StocksClientCommandHandlerBuilder extends CommandHandlerBuilder {

    /**
     * Initialises the command handler builder.
     */
    public StocksClientCommandHandlerBuilder() {
        this.reset();
    }

    /**
     * Adds the commands that the bot needs to the command handler.
     *
     * @param stocksMap Map that contains all the stocks.
     * @param trader    The trader which this bot will behave as.
     */
    public void addCommands(Map<String, Stock> stocksMap, Trader trader) {
        addCommand("broadcast", new ProcessBroadcastCommand(stocksMap, trader));
        addCommand("notification", new LogNotificationCommand(trader));
    }

}