package nl.rug.aoop.stocksapplication.commands;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.stocksapplication.order.BuyOrderQueue;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.stocksapplication.order.SellOrderQueue;

import java.util.*;

/**
 * Processes a buy order by getting the lowest price sell order in the queue and the
 * forwarding the 2 orders on to the next command.
 */
@Slf4j
public class ProcessBuyOrderCommand implements Command {
    private final Command resolveOrdersCommand;
    private final BuyOrderQueue buyOrders;
    private final SellOrderQueue sellOrders;

    /**
     * Initializes the command.
     *
     * @param resolveOrdersCommand The command which will be used to resolve the buy and sell orders.
     * @param buyOrders            The BuyOrderQueue.
     * @param sellOrders           The SellOrderQueue.
     */
    public ProcessBuyOrderCommand(Command resolveOrdersCommand, BuyOrderQueue buyOrders,
                                  SellOrderQueue sellOrders) {
        this.resolveOrdersCommand = resolveOrdersCommand;
        this.buyOrders = buyOrders;
        this.sellOrders = sellOrders;
    }

    @Override
    public void execute(Map<String, Object> options) {
        log.info("Executing buy order...");
        Order buyOrder = Command.retrieveOption("order", Order.class, options);

        Order sellOrder = sellOrders.dequeue(buyOrder.getStock());
        if (sellOrder != null && sellOrder.getPrice() <= buyOrder.getPrice() &&
                sellOrder.getTraderID() != buyOrder.getTraderID()) {
            Map<String, Object> newOptions = createOptionsMap(buyOrder, sellOrder);
            resolveOrdersCommand.execute(newOptions);
            return;
        } else if (sellOrder != null) {
            // Add the sell order back onto the sell order queue.
            sellOrders.enqueue(sellOrder);
        }
        buyOrders.enqueue(buyOrder);
        log.info("No suitable order found. Adding order to buy order queue.");
    }

    /**
     * Creates the options map with the 2 orders.
     *
     * @param buyOrder  Buy orders.
     * @param sellOrder Sell Order.
     * @return The hash map with the 2 orders.
     */
    private Map<String, Object> createOptionsMap(Order buyOrder, Order sellOrder) {
        Map<String, Object> options = new HashMap<>();
        options.put("buyOrder", buyOrder);
        options.put("sellOrder", sellOrder);
        return options;
    }
}
