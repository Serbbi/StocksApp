package nl.rug.aoop.stocksapplication.order;

import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;

/**
 * An {@link OrderQueue} that stores the sell orders. Uses a reversed {@link OrderComparator}.
 */
public class BuyOrderQueue extends OrderQueue {
    /**
     * Initializes the {@link OrderQueue} with the reverse {@link OrderComparator}.
     */
    public BuyOrderQueue() {
        super(new OrderComparator().reversed());
    }

    @Override
    public void enqueue(Order order) {
        if (order.getType() != OrderType.BUY) {
            throw new IllegalArgumentException("Wrong order type!");
        }

        super.enqueue(order);
    }
}
