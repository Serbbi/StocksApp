package nl.rug.aoop.stocksapplication.order;

import nl.rug.aoop.util.order.Order;

import java.util.Comparator;

/**
 * Used to prioritise orders in the {@link OrderQueue}.
 */
public class OrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order o1, Order o2) {
        if (o1.getPrice() < o2.getPrice()) {
            return -1;
        } else if (o1.getPrice() > o2.getPrice()) {
            return 1;
        }
        return 0;
    }

    @Override
    public Comparator<Order> reversed() {
        return Comparator.super.reversed();
    }
}
