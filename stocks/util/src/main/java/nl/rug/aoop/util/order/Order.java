package nl.rug.aoop.util.order;

import com.google.gson.Gson;
import lombok.Getter;

/**
 * Stores all the date for an order.
 */
public class Order {
    @Getter
    private final OrderType type;
    @Getter
    private final String traderID;
    @Getter
    private final String stock;
    @Getter
    private final double price;
    @Getter
    private final Integer numShares;

    /**
     * Main constructor.
     *
     * @param type      The type of the order. Either buy or sell.
     * @param traderID  ID of the trader that placed the order.
     * @param stock     Symbol of stock to buy or sell.
     * @param price     The price which the shares should be bought or sold at.
     * @param numShares Number of shares to be bought or sold.
     */
    public Order(OrderType type, String traderID, String stock, double price, Integer numShares) {
        this.type = type;
        this.traderID = traderID;
        this.stock = stock;
        this.price = price;
        this.numShares = numShares;
    }

    /**
     * Converts the order to JSON.
     *
     * @return JSON of order.
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Converts from json to an order.
     * @param json  json string.
     * @return  order.
     */
    public static Order fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Order.class);
    }

    @Override
    public String toString() {
        return toJson();
    }

}
