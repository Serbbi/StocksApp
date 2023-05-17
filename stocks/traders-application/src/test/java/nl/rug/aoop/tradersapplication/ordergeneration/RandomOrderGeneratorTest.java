package nl.rug.aoop.tradersapplication.ordergeneration;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.tradersapplication.ordergeneration.RandomOrderGenerator;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.stock.StocksLoader;
import nl.rug.aoop.util.trader.StockPortfolio;
import nl.rug.aoop.util.trader.Trader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests the order generator.
 * Tests here aren't very in depth, as due to the fact that they are random, it is hard to assert from the tests.
 */
@Slf4j
class RandomOrderGeneratorTest {
    private Map<String, Stock> stockMap;
    private Trader trader;

    /**
     * Sets up the trader and stocks map to be used for order generation.
     */
    @BeforeEach
    void setUp() {
        StockPortfolio portfolio = new StockPortfolio();
        portfolio.addShares("MRNA", 10);
        portfolio.addShares("NVDA", 20);

        trader = Mockito.mock(Trader.class);
        when(trader.getId()).thenReturn("bot1");
        when(trader.getFunds()).thenReturn(10000.0);
        when(trader.getStockPortfolio()).thenReturn(portfolio);

        stockMap = new HashMap<String, Stock>();
        stockMap.put("MRNA", new Stock("MRNA", "Moderna", 1000L, 50.0));
        stockMap.put("MRNA", new Stock("NVDA", "Nvidia", 1000L, 50.0));
    }

    /**
     * Tests that the random order generation produces an order.
     */
    @Test
    void testGenerateRandomOrder() {
        RandomOrderGenerator rog = new RandomOrderGenerator(stockMap, trader);
        Order order = rog.generateOrder();

        assertNotNull(order);
    }

    /**
     * Tests that the random order generation produces a buy order.
     */
    @Test
    void testGenerateRandomBuyOrder() {
        RandomOrderGenerator rog = new RandomOrderGenerator(stockMap, trader);
        Order order = rog.generateOrder(OrderType.BUY);
        log.info(order.getStock());

        assertEquals(order.getType(), OrderType.BUY);
        assertTrue(order.getNumShares() >= 0);
        assertTrue(order.getNumShares() <= (trader.getFunds() / order.getPrice()));
    }


    /**
     * Tests that the random order generation produces a buy order.
     */
    @Test
    void testGenerateRandomSellOrder() {
        RandomOrderGenerator rog = new RandomOrderGenerator(stockMap, trader);
        Order order = rog.generateOrder(OrderType.SELL);

        assertEquals(order.getType(), OrderType.SELL);
        assertTrue(order.getNumShares() > 0);
        assertTrue(trader.getStockPortfolio().getOwnedShares().containsKey(order.getStock()));
        log.info(order.getStock());
    }
}