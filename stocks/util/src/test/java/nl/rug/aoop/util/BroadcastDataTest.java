package nl.rug.aoop.util;

import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.StockPortfolio;
import nl.rug.aoop.util.trader.Trader;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BroadcastDataTest {
    @Test
    void testToFromJson() {
        Map<String, Stock> stocksMap = new HashMap<>();
        stocksMap.put("symbol", new Stock("symbol", "name", (long)10, 10.0));
        Trader trader = new Trader("id", "name", 10, new StockPortfolio());
        BroadcastData broadcastData = new BroadcastData(stocksMap, trader);
        String str = broadcastData.toJson();
        assertNotNull(str);

        BroadcastData newBroadcastData = BroadcastData.fromJson(str);
        assertEquals(newBroadcastData.getStocks().get("symbol").getName(),"name");
        assertEquals(newBroadcastData.getTrader().getId(), "id");
    }
}
