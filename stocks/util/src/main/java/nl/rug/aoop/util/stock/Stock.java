package nl.rug.aoop.util.stock;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.model.StockDataModel;

/**
 * Stores all the data for a stock.
 */
@Slf4j
public class Stock implements StockDataModel {
    private final String symbol;
    private final String name;
    private final long sharesOutstanding;
    private double price;

    /**
     * Main Constructor.
     *
     * @param symbol            A unique identifier for the stock. Often resembles the name of the company.
     * @param name              The name of the corporation behind the stock.
     * @param sharesOutstanding The number of shares that are held by all its shareholders.
     * @param price             The price of a single share. As detailed later on,
     *                          this is the price of its latest transaction.
     */
    public Stock(String symbol, String name, long sharesOutstanding, double price) {
        this.symbol = symbol;
        this.name = name;
        this.sharesOutstanding = sharesOutstanding;
        this.price = price;
    }

    /**
     * Sets the most recent sale price for the stock.
     *
     * @param price Price to be set.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Calculates the marketCapitalization of the stock.
     * The market capitalization is the total market value of the company's outstanding shares.
     * Calculated as `market cap = shares outstanding * price`.
     *
     * @return Market cap of stock.
     */
    @Override
    public double getMarketCap() {
        return sharesOutstanding * price;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSharesOutstanding() {
        return sharesOutstanding;
    }

    @Override
    public double getPrice() {
        return price;
    }
}
