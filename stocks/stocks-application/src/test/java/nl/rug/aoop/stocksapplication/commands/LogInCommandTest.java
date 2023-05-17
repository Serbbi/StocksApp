package nl.rug.aoop.stocksapplication.commands;

import nl.rug.aoop.util.trader.StockPortfolio;
import nl.rug.aoop.util.trader.Trader;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogInCommandTest {
    private Map<String, Trader> tradersMap = new HashMap<>();

    @Test
    void executeTest() {
        Map<String, Object> options = new HashMap<>();
        options.put("trader", new Trader("name", "id", 10.0, new StockPortfolio()));
        LogInCommand logInCommand = new LogInCommand(tradersMap);
        logInCommand.execute(options);

        assertEquals(tradersMap.size(), 1);
    }
}
