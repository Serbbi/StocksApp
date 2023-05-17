package nl.rug.aoop.util.trader;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.model.TraderDataModel;
import nl.rug.aoop.util.order.OrderType;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.Transaction;

import java.util.*;

/**
 * Holds all the data for a trader.
 */
@Slf4j
public class Trader implements TraderDataModel {
    @Getter
    private String name;
    @Getter
    private String id;
    @Getter
    private double funds;
    @Getter
    private List<Transaction> transactionHistory;
    @Getter
    private StockPortfolio stockPortfolio;

    /**
     * Main Constructor.
     *
     * @param name               Name of the trader.
     * @param id                 Unique identifier for the trader.
     * @param funds              The available funds of the trader.
     * @param transactionHistory A list of previous transactions that have been made by the trader.
     * @param stockPortfolio     A hash map of the number of shares owned.
     */
    public Trader(String name, String id, double funds, List<Transaction> transactionHistory,
                  StockPortfolio stockPortfolio) {
        this.name = name;
        this.id = id;
        this.funds = funds;
        this.transactionHistory = transactionHistory;
        this.stockPortfolio = stockPortfolio;
    }

    /**
     * Default constructor.
     */
    public Trader() {
        transactionHistory = new ArrayList<>();
    }

    /**
     * Constructor for the trader.
     *
     * @param name           Name of the trader.
     * @param id             Unique identifier for the trader.
     * @param funds          The available funds of the trader.
     * @param stockPortfolio A hash map of the number of shares owned.
     */
    public Trader(String id, String name, double funds, StockPortfolio stockPortfolio) {
        this.id = id;
        this.name = name;
        this.funds = funds;
        this.stockPortfolio = stockPortfolio;
        this.transactionHistory = new ArrayList<>();
    }

    /**
     * Adds a transaction to the transaction history.
     *
     * @param transaction Transaction to add.
     */
    public void addTransaction(Transaction transaction) {
        double amount = ((transaction.getType() == OrderType.BUY) ? -1 : 1)
                * transaction.getPrice() * transaction.getNumShares();

        funds += amount;

        if (transaction.getType() == OrderType.BUY) {
            stockPortfolio.addShares(transaction.getStock(), transaction.getNumShares());
        } else {
            stockPortfolio.removeShares(transaction.getStock(), transaction.getNumShares());
        }

        this.transactionHistory.add(transaction);
    }

    /**
     * Returns the number of transactions in the transaction history.
     *
     * @return Number of transactions in the transaction history.
     */
    public Integer getTransactionCount() {
        return transactionHistory.size();
    }

    /**
     * Adds funds to the Trader.
     *
     * @param funds Amount of funds being added.
     */
    public void addFunds(double funds) {
        this.funds += funds;
    }

    /**
     * returns the number of a shares of a stock that the trader owns.
     *
     * @param stock Stock to get share count for.
     * @return Number of shares owned of the specified stock.
     */
    public Integer getNumberOfSharesOwned(String stock) {
        return stockPortfolio.getStock(stock);
    }

    /**
     * Checks that the trader has sufficient funds to purchase the specified shares of a stock.
     *
     * @param price     Price at which shares are being purchased at.
     * @param numShares Number of shares being purchased.
     * @return True if the trader has enough funds to purchase the shares. False otherwise.
     */
    public boolean checkSufficientFundsForPurchase(double price, Integer numShares) {
        return funds >= (numShares * price);
    }

    /**
     * Checks if the trader has enough shares of a stock to perform a sale.
     *
     * @param stock     Stock being purchased.
     * @param numShares Number of shares being purchased.
     * @return True if the trader has enough shares to sell. False otherwise.
     */
    public boolean checkSufficientSharesForSale(Stock stock, Integer numShares) {
        return getNumberOfSharesOwned(stock.getSymbol()) >= numShares;
    }

    /**
     * Checks if the trader has enough shares of a stock to perform a sale.
     *
     * @param stockSymbol Symbol of Stock being purchased.
     * @param numShares   Number of shares being purchased.
     * @return True if the trader has enough shares to sell. False otherwise.
     */
    public boolean checkSufficientSharesForSale(String stockSymbol, Integer numShares) {
        return getNumberOfSharesOwned(stockSymbol) >= numShares;
    }

    @Override
    public List<String> getOwnedStocks() {
        List<String> list = new ArrayList<>();
        stockPortfolio.getOwnedShares().forEach((string, integer) -> list.add(string));
        return list;
    }

    @Override
    public int getNumberOfOwnedShares(String stockSymbol) {
        return stockPortfolio.getStock(stockSymbol);
    }

    /**
     * Used to update the trader, without changing the trader reference.
     *
     * @param trader Trader object to get the new data from.
     */
    public void update(Trader trader) {
        this.name = trader.getName();
        this.id = trader.getId();
        this.funds = trader.getFunds();
        this.transactionHistory = trader.getTransactionHistory();
        this.stockPortfolio = trader.getStockPortfolio();

    }

    /**
     * Gets a string representation of trader. For debugging purposes.
     *
     * @return String representation of trader.
     */
    public String toString() {
        return "Trader{" + "name='" + name + '\'' + ", id='" + id + '\'' + ", funds=" + funds + ", " +
                "transactionHistory=" + transactionHistory + ", stockPortfolio=" + stockPortfolio + '}';
    }
}
