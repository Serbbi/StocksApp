package nl.rug.aoop.stocksapplication.order;

import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;

import java.util.Map;
import java.util.Queue;

/**
 * An {@link OrderQueue} that stores the sell orders. Uses a {@link OrderComparator}.
 */
public class SellOrderQueue extends OrderQueue {

    private Map<String, Queue<Order>> stockOrders;

    /**
     * Initializes the {@link OrderQueue} with the {@link OrderComparator}.
     */
    public SellOrderQueue() {
        super(new OrderComparator());
    }

    @Override
    public void enqueue(Order order) {
        if (order.getType() != OrderType.SELL) {
            throw new IllegalArgumentException("Wrong order type!");
        }
        super.enqueue(order);
    }
}
