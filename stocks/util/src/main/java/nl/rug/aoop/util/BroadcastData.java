package nl.rug.aoop.util;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.Trader;

import java.util.Map;

/**
 * A record used to easily convert the data to json.
 */
@Slf4j
public class BroadcastData {
    @Getter
    private Trader trader;
    @Getter
    private Map<String, Stock> stocks;

    /**
     * Main Constructor.
     *
     * @param stocks List of stocks to be converted to json.
     * @param trader Trader for which the data should be converted to json.
     */
    public BroadcastData(Map<String, Stock> stocks, Trader trader) {
        this.stocks = stocks;
        this.trader = trader;
    }

    /**
     * Gets a string json representation of the broadcast data.
     *
     * @return String JSON representation of the broadcast data.
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Converts the string broadcast data to a BroadcastData object.
     *
     * @param json String json broadcast data.
     * @return BroadcastData object.
     */
    public static BroadcastData fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, BroadcastData.class);
    }
}
