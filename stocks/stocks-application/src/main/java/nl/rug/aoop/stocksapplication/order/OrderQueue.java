package nl.rug.aoop.stocksapplication.order;

import nl.rug.aoop.util.order.Order;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Uses a hashmap of queues to store orders, based on the stock they are for.
 * The orders are stored in priority queues, and the priority is defined by the comparator that is passed in.
 */
public abstract class OrderQueue {
    private final Map<String, PriorityBlockingQueue<Order>> stockOrders;
    private final Comparator<Order> orderComparator;

    /**
     * Initialises the order queue.
     *
     * @param orderComparator The comparator used to define the order in which the orders are
     *                        removed from the subqueues.
     */
    public OrderQueue(Comparator<Order> orderComparator) {
        stockOrders = new ConcurrentHashMap<>();
        this.orderComparator = orderComparator;
    }

    /**
     * Adds order to the queue corresponding to the stock which it is trying to purchase.
     * If key exists in map, then order added to that queue. Adds new queue to map if the key doesn't exist.
     *
     * @param order Thee order to enqueue.
     */
    public void enqueue(Order order) {
        if (stockOrders.containsKey(order.getStock())) {
            stockOrders.get(order.getStock()).add(order);
        } else {
            PriorityBlockingQueue<Order> q = new PriorityBlockingQueue<>(5, orderComparator);
            q.add(order);
            stockOrders.put(order.getStock(), q);
        }
    }

    /**
     * Gets the highest priority order for a particular stock.
     *
     * @param stockSymbol Stock for which an order needs to be retrieved.
     * @return The highest priority order for a particular stock.
     */
    public Order dequeue(String stockSymbol) {
        if (stockOrders.containsKey(stockSymbol)) {
            // Used poll as it returns null if order queue empty;
            return stockOrders.get(stockSymbol).poll();
        } else {
            return null;
        }
    }

    /**
     * Counts the number of orders.
     *
     * @return Total number of orders.
     */
    public int getSize() {
        int size = 0;
        for (Queue<Order> q : stockOrders.values()) {
            size += q.size();
        }
        return size;
    }

    /**
     * Counts the number of orders for a particular stock.
     *
     * @param stockSymbol Symbol of the stock to check the number of orders for.
     * @return Total number of orders for a particular stock.
     */
    public int getSize(String stockSymbol) {
        if (stockOrders.containsKey(stockSymbol)) {
            return stockOrders.get(stockSymbol).size();
        } else {
            return 0;
        }
    }
}
