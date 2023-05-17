package nl.rug.aoop.tradersapplication.ordergeneration;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.StockPortfolio;
import nl.rug.aoop.util.trader.Trader;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates random orders.
 */
@Slf4j
public class RandomOrderGenerator implements OrderGenerator {

    private final Map<String, Stock> stocksMap;
    private final Trader trader;

    /**
     * Main constructor.
     *
     * @param stocksMap Stocks map.
     * @param trader    The trader that is making the order.
     */
    public RandomOrderGenerator(Map<String, Stock> stocksMap, Trader trader) {
        this.stocksMap = stocksMap;
        this.trader = trader;
    }

    /**
     * Gets a random number between min and (max - 1).
     *
     * @param min Minimum bound.
     * @param max Maximum bound.
     * @return The random number between min and (max - 1).
     */
    private Integer getRandInt(int min, int max) {
        if (max > min) {
            return ThreadLocalRandom.current().nextInt(min, max); // Exclusive of top num.
        }
        throw new IllegalArgumentException("Invalid number between min and max.");
    }

    /**
     * Creates a random buy or sell order and sends it to the server.
     */
    @Override
    public Order generateOrder() {
        if (stocksMap.size() == 0) {
            log.warn("No stocks in map. Skipping order generation.");
            return null;
        }
        // Randomize buy or sell
        int randomNum = getRandInt(0, 2); // Exclusive of top num.
        OrderType type = randomNum == 0 ? OrderType.BUY : OrderType.SELL;
        return generateOrder(type);
    }

    @Override
    public Order generateOrder(OrderType type) {
        return (type == OrderType.BUY) ? getRandomBuyOrder() : getRandomSellOrder();
    }

    /**
     * Picks a random stock symbol from a map.
     *
     * @param map Map which has the stock symbol as the key.
     * @param <E> Any type, just to ignore the value of the map.
     * @return String symbol for a random stock.
     */
    private <E> String pickRandomStock(Map<String, E> map) {
        List<String> lst = map.keySet().stream().toList();
        int keyNum = getRandInt(0, lst.size());
        return lst.get(keyNum);
    }

    /**
     * Creates a random buy order.
     * Chooses a random stock from the stocks map.
     * Gets a price by taking the current price and changing it slightly. Explained later.
     * Gets a random number of shares to purchase between 0 and max can be bought with current funds.
     *
     * @return Order from all the random values generated.
     */
    private Order getRandomBuyOrder() {
        // Choose random stock
        String stockSymbol = pickRandomStock(stocksMap);
        log.info(stockSymbol);
        Stock stock = stocksMap.get(stockSymbol);

        // Randomizes the price slightly. Price can go down by up to 5% or up by up to 10%.
        double pricePercentageChange = getRandInt(0, 20) - 5;
        double price = stock.getPrice();
        price += price * (pricePercentageChange / 100);

        int maxNumStocks = (int) Math.floor(trader.getFunds() / price);

        // Case where trader doesn't have enough funds.
        if (maxNumStocks <= 0) {
            return null;
        }

        int numShares = getRandInt(1, maxNumStocks);

        return new Order(OrderType.BUY, trader.getId(), stock.getSymbol(), price, numShares);
    }

    /**
     * Creates a random sell order.
     * Chooses a random stock from the stocks the trader owns.
     * Gets a price by taking the current price and changing it slightly. Explained later.
     * Gets a random number of shares to purchase between 0 and number of that stock that the trader owns.
     *
     * @return Order from all the random values generated.
     */
    private Order getRandomSellOrder() {
        StockPortfolio portfolio = trader.getStockPortfolio();
        if (portfolio.getOwnedShares().size() == 0) {
            return null;
        }

        Map<String, Integer> ownedShares = portfolio.getOwnedShares();

        String stockSymbol = pickRandomStock(ownedShares);
        Stock stock = stocksMap.get(stockSymbol);

        int numSharesOwned = ownedShares.get(stockSymbol);

        // Randomizes the price slightly. Price can go down by up to 10% or up by up to 5%.
        double pricePercentageChange = getRandInt(0, 20) - 10;

        double price = stock.getPrice();
        price += price * (pricePercentageChange / 100);

        int numShares = getRandInt(1, numSharesOwned);

        return new Order(OrderType.SELL, trader.getId(), stock.getSymbol(), price, numShares);
    }
}
