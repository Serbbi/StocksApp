package nl.rug.aoop.util.trader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class StockPortfolioTest {
    @Test
    void addNewStockTest() {
        String stockSymbol = "stock";
        Integer value = 10;

        StockPortfolio stockPortfolio = new StockPortfolio();
        stockPortfolio.addShares(stockSymbol,10);
        assertEquals(stockPortfolio.getOwnedShares().size(),1);
    }

    @Test
    void addOldStockTest() {
        String stockSymbol = "stock";
        Integer value = 10;

        StockPortfolio stockPortfolio = new StockPortfolio();
        stockPortfolio.addShares(stockSymbol,value);
        stockPortfolio.addShares(stockSymbol,value);

        assertEquals(stockPortfolio.getOwnedShares().size(),1);
        assertEquals(stockPortfolio.getOwnedShares().get(stockSymbol),2*value);
    }

    @Test
    void getExistingStock() {
        String stockSymbol = "stock";
        Integer value = 10;

        StockPortfolio stockPortfolio = new StockPortfolio();
        stockPortfolio.addShares(stockSymbol,value);

        assertSame(stockPortfolio.getStock(stockSymbol),value);
    }

    @Test
    void getNonExistingStock() {
        String stockSymbol = "stock";
        StockPortfolio stockPortfolio = new StockPortfolio();

        assertSame(stockPortfolio.getStock(stockSymbol),0);
    }
}
