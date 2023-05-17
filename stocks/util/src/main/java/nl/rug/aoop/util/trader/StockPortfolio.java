package nl.rug.aoop.util.trader;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * StockPortfolio keeps track of all the stocks and shares a trader has.
 */
@Slf4j
public class StockPortfolio {
    @Getter
    private final Map<String, Integer> ownedShares;

    /**
     * Constructor for the portfolio.
     */
    public StockPortfolio() {
        this.ownedShares = new HashMap<>();
    }

    /**
     * Method to add stock to the portfolio.
     *
     * @param key   the stock's symbol.
     * @param value how many shares.
     */
    public void addShares(String key, Integer value) {
        if (ownedShares.containsKey(key)) {
            ownedShares.put(key, ownedShares.get(key) + value);
        } else {
            ownedShares.put(key, value);
        }
    }

    /**
     * Removes the sepcified number of shares for a particular stock.
     *
     * @param key    Stock symbol.
     * @param amount Number of shares to remove.
     */
    public void removeShares(String key, Integer amount) {
        Integer current = ownedShares.get(key);
        if (current == null) {
            log.error("Could not remove shares because stock not found!");
            return;
        }
        if (amount < current) {
            ownedShares.put(key, current - amount);
        } else if (amount == current) {
            ownedShares.remove(key);
        } else {
            throw new RuntimeException("Invalid number of shares.");
        }
    }

    /**
     * Method to get a count of shares for a particular stock.
     *
     * @param key the stock's symbol.
     * @return COunt of the number of shares of the stock that are owned.
     */
    public Integer getStock(String key) {
        if (ownedShares.containsKey(key)) {
            return ownedShares.get(key);
        }
        return 0;
    }
}
