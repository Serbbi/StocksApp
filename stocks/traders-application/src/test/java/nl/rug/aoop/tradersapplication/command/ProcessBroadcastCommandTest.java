package nl.rug.aoop.tradersapplication.command;

import nl.rug.aoop.util.BroadcastData;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.StockPortfolio;
import nl.rug.aoop.util.trader.Trader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessBroadcastCommandTest {
    @Test
    void testExecute() {
        Trader trader = new Trader("id", "name", 10, new StockPortfolio());
        Map<String, Stock> stockMap = new HashMap<>();
        stockMap.put("symbol", new Stock("symbol", "name", (long)10, 10.0));
        BroadcastData bd = new BroadcastData(stockMap, trader);
        Map<String, Object> options = new HashMap<>();
        options.put("value", bd.toJson());

        Map<String,Stock> newStocksMap = new HashMap<>();
        Trader newTrader = new Trader();
        ProcessBroadcastCommand pbc = new ProcessBroadcastCommand(newStocksMap, newTrader);
        pbc.execute(options);

        assertEquals(stockMap.get("symbol").getName(), "name");
        assertEquals(newTrader.getFunds(),trader.getFunds());
    }
}
