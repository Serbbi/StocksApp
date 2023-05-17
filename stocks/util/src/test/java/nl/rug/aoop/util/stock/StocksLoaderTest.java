package nl.rug.aoop.util.stock;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class StocksLoaderTest {

    @Test
    void testStockLoader() {
        Map<String, Stock> stockMap = StocksLoader.getMapFromYaml("stocks");

        stockMap.forEach((key, stock) -> {
            assertInstanceOf(Stock.class, stock);
            assertNotNull(stock.getName());
            log.info(stock.getName());
        });
    }
}