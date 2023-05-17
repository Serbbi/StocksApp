package nl.rug.aoop.util.trader;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.stock.StocksLoader;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TradersLoaderTest {
    @Test
    void testTraderLoader() {
        Map<String, Trader> traderMap = TradersLoader.getMapFromYaml("traders");

        traderMap.forEach((key, trader) -> {
            assertInstanceOf(Trader.class, trader);
            assertNotNull(trader.getName());
            log.info(trader.getName());
        });
    }
}