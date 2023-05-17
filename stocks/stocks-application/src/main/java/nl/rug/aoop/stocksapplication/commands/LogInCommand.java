package nl.rug.aoop.stocksapplication.commands;

import nl.rug.aoop.command.Command;
import nl.rug.aoop.util.trader.Trader;

import java.util.Map;

/**
 * Command for logging in.
 */
public class LogInCommand implements Command {
    private final Map<String, Trader> tradersMap;

    /**
     * Constructor for the command.
     * @param tradersMap    trader map.
     */
    public LogInCommand(Map<String, Trader> tradersMap) {
        this.tradersMap = tradersMap;
    }

    @Override
    public void execute(Map<String, Object> options) {
        Trader trader = Command.retrieveOption("trader",Trader.class,options);
        tradersMap.put(trader.getId(),trader);
    }
}
