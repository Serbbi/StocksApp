package nl.rug.aoop.util;

import com.google.gson.Gson;
import lombok.Getter;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;

import java.util.UUID;

/**
 * Stores transaction data.
 */
public class Transaction {
    @Getter
    private UUID uuid;
    @Getter
    private String stock;
    @Getter
    private OrderType type;
    @Getter
    private double price;
    @Getter
    private Integer numShares;

    /**
     * Main Constructor for the transaction.
     * @param uuid      the id of the transaction.
     * @param stock     stock symbol.
     * @param type      type of the order.
     * @param price     price of the stock bought/sold.
     * @param numShares shares bought/sold.
     */
    public Transaction(UUID uuid, String stock, OrderType type, double price, Integer numShares) {
        this.uuid = uuid;
        this.stock = stock;
        this.type = type;
        this.price = price;
        this.numShares = numShares;
    }

    /**
     * Constructor that generates the UUID.
     *
     * @param stock     Stock symbol.
     * @param type      Type of transaction.
     * @param price     Price used in transaction.
     * @param numShares Number of shares.
     */
    public Transaction(String stock, OrderType type, double price, Integer numShares) {
        this(UUID.randomUUID(), stock, type, price, numShares);
    }

    /**
     * Creates a transtaction using an order.
     *
     * @param order Order to use to create the transaction.
     */
    public Transaction(Order order) {
        this(order.getStock(), order.getType(), order.getPrice(), order.getNumShares());
    }

    /**
     * Converts the object to a json string.
     *
     * @return Json string representation of object.
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
