package nl.rug.aoop.tradersapplication.command;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.util.BroadcastData;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.Trader;

import java.util.Map;

/**
 * Processes teh broadcast received from the server.
 */
@Slf4j
public class ProcessBroadcastCommand implements Command {

    private Map<String, Stock> stockMap;
    private Trader trader;

    /**
     * Main constructor.
     *
     * @param stocksMap Map that holds all the stock data. Will be updated.
     * @param trader    Trader that should be updated from the broadcast.
     */
    public ProcessBroadcastCommand(Map<String, Stock> stocksMap, Trader trader) {
        this.trader = trader;
        this.stockMap = stocksMap;
    }

    @Override
    public void execute(Map<String, Object> options) {
        String bdStr = Command.retrieveOption("value", String.class, options);
        BroadcastData bd = BroadcastData.fromJson(bdStr);
        trader.update(bd.getTrader());
        stockMap.clear();
        stockMap.putAll(bd.getStocks());
    }
}
