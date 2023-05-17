package nl.rug.aoop.tradersapplication.ordergeneration;

import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;

/**
 * Generates orders.
 */
public interface OrderGenerator {

    /**
     * Generates a random buy or sell order.
     *
     * @return A generated order.
     */
    Order generateOrder();

    /**
     * Generates a random order with type.
     *
     * @param orderType Type that the order should be.
     * @return Order of {@link OrderType} type.
     */
    Order generateOrder(OrderType orderType);
}
