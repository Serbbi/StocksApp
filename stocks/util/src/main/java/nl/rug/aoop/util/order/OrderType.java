package nl.rug.aoop.util.order;

import com.google.gson.annotations.SerializedName;

/**
 * Used to store the order type.
 */
public enum OrderType {
    @SerializedName("BUY")
    BUY,
    @SerializedName("SELL")
    SELL
}
