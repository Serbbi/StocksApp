package nl.rug.aoop.util.trader;

import nl.rug.aoop.util.stock.Stock;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the trader class.
 */
class TraderTest {
    private final String traderName = "John Doe";
    private final double funds = 100.10;

    private Stock getMockStock(String symbol) {
        Stock stock = Mockito.mock(Stock.class);
        when(stock.getSymbol()).thenReturn(symbol);
        return stock;
    }

    /**
     * Tests that the constructor generates the UUID, and that all fields are set.
     */
    @Test
    void testConstructor() {
        Trader trader = new Trader(traderName, "traderID", funds, new ArrayList<>(), new StockPortfolio());

        assertNotNull(trader.getId());
        assertEquals(traderName, this.traderName);
        assertEquals(funds, this.funds);
    }

    @Test
    void testAddFunds() {
        Trader trader = new Trader(traderName, "traderID", funds, new ArrayList<>(), new StockPortfolio());
        double newFunds = 55.55;
        trader.addFunds(newFunds);

        assertEquals(trader.getFunds(), this.funds + newFunds);
    }

    @Test
    void testCheckSufficientFundsForPurchase() {
        Map<String, Integer> stocksOwned = new HashMap<String, Integer>();

        String stockSymbol = "ABC";
        stocksOwned.put(stockSymbol, 4);
        Trader trader = new Trader(traderName, "traderID", funds, new ArrayList<>(), new StockPortfolio());

        assertTrue(trader.checkSufficientFundsForPurchase(5, 3));
        assertTrue(trader.checkSufficientFundsForPurchase(50.05, 2));

        assertFalse(trader.checkSufficientFundsForPurchase(50, 3));
    }

    @Test
    void testCheckSufficientSharesForSale() {
        StockPortfolio stockPortfolio = new StockPortfolio();

        String stockSymbol = "ABC";
        stockPortfolio.addShares(stockSymbol, 4);

        Trader trader = new Trader(traderName, "traderID", funds, new ArrayList<>(), stockPortfolio);

        Stock stock = getMockStock(stockSymbol);

        assertTrue(trader.checkSufficientSharesForSale(stock, 3));
        assertTrue(trader.checkSufficientSharesForSale(stock, 4));
        assertFalse(trader.checkSufficientSharesForSale(stock, 5));
    }

}