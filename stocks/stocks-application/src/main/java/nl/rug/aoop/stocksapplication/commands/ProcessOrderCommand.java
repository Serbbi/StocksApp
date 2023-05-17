package nl.rug.aoop.stocksapplication.commands;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.Trader;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;

import java.util.HashMap;
import java.util.Map;

/**
 * Processes an order. Checks if the trader can satisfy the order, and if so, forwards the order on the
 * {@link ProcessBuyOrderCommand} or {@link ProcessSellOrderCommand}.
 */
@Slf4j
public class ProcessOrderCommand implements Command {
    private final CommandHandler handler;
    private final Map<String, Trader> traderMap;
    private final Map<String, Stock> stocksMap;

    /**
     * Initializes the command.
     *
     * @param handler   The command handler which has the process buy order and sell order commands in it.
     * @param traderMap Hash map with all the traders in it.
     * @param stocksMap Hash map with all the stocks in it.
     */
    public ProcessOrderCommand(CommandHandler handler, Map<String, Trader> traderMap, Map<String, Stock> stocksMap) {
        this.handler = handler;
        this.traderMap = traderMap;
        this.stocksMap = stocksMap;
    }

    @Override
    public void execute(Map<String, Object> options) {
        Object value = options.get("value");
        Order order;
        if (value.getClass() == Order.class) {
            order = Command.retrieveOption("value", Order.class, options);
        } else {
            String orderstr = Command.retrieveOption("value", String.class, options);
            order = Order.fromJson(orderstr);
        }
        log.info(order.toString());

        Trader trader = traderMap.get(order.getTraderID());
        Map<String,Object> newOptions = new HashMap<>();
        newOptions.put("order", order);

        // Passed same options as there is no need to change the option name.
        if (order.getType() == OrderType.BUY) {
            if (trader.checkSufficientFundsForPurchase(order.getPrice(), order.getNumShares())) {
                handler.executeCommand("processBuyOrder", newOptions);
            }
        } else {
            Stock stock = stocksMap.get(order.getStock());
            if (trader.checkSufficientSharesForSale(stock, order.getNumShares())) {
                handler.executeCommand("processSellOrder", newOptions);
            }
        }
    }
}
