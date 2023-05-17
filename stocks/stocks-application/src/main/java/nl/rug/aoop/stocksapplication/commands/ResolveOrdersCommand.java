package nl.rug.aoop.stocksapplication.commands;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.Trader;
import nl.rug.aoop.util.Transaction;
import nl.rug.aoop.stocksapplication.broadcast.Broadcaster;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * Satisifies a but and sell order that are proviced. If there are still stocks left in either order,
 * then the order is pass back into the {@link ProcessOrderCommand} to be further resolved.
 */
@Slf4j
public class ResolveOrdersCommand implements Command {
    private final Command processOrderCommand;
    private final Map<String, Trader> tradersMap;
    private final Map<String, Stock> stocksMap;
    private final Broadcaster broadcaster;

    /**
     * Initializes the order command.
     *
     * @param processOrderCommand The command that will be used to process the remaining order.
     * @param tradersMap          The map containing all the traders. Used to add the transactions to the traders.
     * @param stocksMap           The map containing all the stocks.
     * @param broadcaster         Broadcaster to notify traders of resolved commands.
     */
    public ResolveOrdersCommand(Command processOrderCommand, Map<String, Trader> tradersMap,
                                Map<String, Stock> stocksMap, Broadcaster broadcaster) {
        this.processOrderCommand = processOrderCommand;
        this.tradersMap = tradersMap;
        this.stocksMap = stocksMap;
        this.broadcaster = broadcaster;
    }

    @Override
    public void execute(Map<String, Object> options) {
        Order buyOrder = Command.retrieveOption("buyOrder", Order.class, options);
        Order sellOrder = Command.retrieveOption("sellOrder", Order.class, options);

        Trader buyer = tradersMap.get(buyOrder.getTraderID());
        Trader seller = tradersMap.get(sellOrder.getTraderID());

        if (!checkOrders(buyOrder, buyer, sellOrder, seller)) {
            log.info("checkOrders!!!");
            return;
        }

        updateStockPrice(sellOrder.getStock(), sellOrder.getPrice());
        resolveOrders(buyOrder, buyer, sellOrder, seller);
    }

    private boolean checkOrders(Order buyOrder, Trader buyer, Order sellOrder, Trader seller) {
        boolean valid = true;
        if (!checkOrder(buyer, buyOrder)) {
            String notification = format("{ 'message': '%s', 'order': '%s' }",
                    "Failed to resolve order! Not enough funds!", buyOrder.toJson());
            sendResponse(buyer.getId(), notification);

            processRemainingOrder(sellOrder);
            valid = false;
        }

        if (!checkOrder(seller, sellOrder)) {
            String notification = format("{ 'message': '%s', 'order': '%s' }",
                    "Failed to resolve order! Not enough stocks to sell!", sellOrder.toJson());
            sendResponse(seller.getId(), notification);

            processRemainingOrder(buyOrder);
            valid = false;
        }
        return valid;
    }

    private boolean checkOrder(Trader trader, Order order) {
        if (order.getType() == OrderType.BUY) {
            return trader.checkSufficientFundsForPurchase(order.getPrice(), order.getNumShares());
        } else {
            return trader.checkSufficientSharesForSale(order.getStock(), order.getNumShares());
        }
    }

    /**
     * Method for updating the price of the stock after a transaction.
     *
     * @param symbol the symbol of the stock.
     * @param price  the new price of the stock.
     */
    private void updateStockPrice(String symbol, double price) {
        if (stocksMap.containsKey(symbol)) {
            stocksMap.get(symbol).setPrice(price);
        } else {
            log.error("Stock does not exist!");
        }
    }

    private void resolveOrders(Order buyOrder, Trader buyer, Order sellOrder, Trader seller) {
        if (buyOrder.getNumShares() != sellOrder.getNumShares()) {
            handleMismatchedNumShares(buyOrder, buyer, sellOrder, seller);
        } else {
            // Add transactions to both for the case where the order amounts are equal.
            addTransaction(buyer, new Transaction(buyOrder), buyOrder);
            addTransaction(seller, new Transaction(sellOrder), sellOrder);
        }
    }

    /**
     * Method for handling a mismatched number of shares between the two orders.
     *
     * @param buyOrder  the buy order.
     * @param buyer     buyer.
     * @param sellOrder the sell order.
     * @param seller    seller.
     */
    private void handleMismatchedNumShares(Order buyOrder, Trader buyer, Order sellOrder, Trader seller) {
        Transaction buyerTransaction;
        Transaction sellerTransaction;
        Order remainingOrder;
        if (buyOrder.getNumShares() > sellOrder.getNumShares()) {
            remainingOrder = new Order(OrderType.BUY, buyOrder.getTraderID(), buyOrder.getStock(),
                    buyOrder.getPrice(), buyOrder.getNumShares() - sellOrder.getNumShares());
            Order boughtOrder = new Order(OrderType.BUY, buyOrder.getTraderID(),
                    buyOrder.getStock(), sellOrder.getPrice(), sellOrder.getNumShares());
            // Add full transaction to seller as their order has been fulfilled.
            // Add the transaction for the number of shares that the seller sold.
            buyerTransaction = new Transaction(boughtOrder);
            sellerTransaction = new Transaction(sellOrder);
        } else {
            remainingOrder = new Order(OrderType.SELL, sellOrder.getTraderID(), sellOrder.getStock(),
                    sellOrder.getPrice(), sellOrder.getNumShares() - buyOrder.getNumShares());
            Order soldOrder = new Order(OrderType.SELL, buyOrder.getTraderID(), buyOrder.getStock(),
                    buyOrder.getPrice(), buyOrder.getNumShares());
            // Adds full transaction to buyer as their order has been fulfilled.
            // Adds the transaction for the number of shares that the buyer bought.
            buyerTransaction = new Transaction(buyOrder);
            sellerTransaction = new Transaction(soldOrder);
        }
        addTransaction(buyer, buyerTransaction, buyOrder);
        addTransaction(seller, sellerTransaction, sellOrder);
        processRemainingOrder(remainingOrder);
    }

    /**
     * Creates the options map with the remaining order, and passes it to the process order commend to be processed.
     *
     * @param order Order which must be processed.
     */
    private void processRemainingOrder(Order order) {
        Map<String, Object> newMap = new HashMap<>();
        newMap.put("value", order);
        processOrderCommand.execute(newMap);
    }

    private void addTransaction(Trader trader, Transaction transaction, Order order) {
        Boolean partial = order.getNumShares() != transaction.getNumShares();
        trader.addTransaction(transaction);

        Map<String, String> notificationMap = new HashMap<>();
        notificationMap.put("message", partial ? "Order Partially Resolved!" : "Order Resolved!");
        notificationMap.put("transaction", transaction.toJson());

        Gson gson = new Gson();
        sendResponse(trader.getId(), gson.toJson(notificationMap));
    }

    /**
     * Method to send responses to trader when order resolved.
     *
     * @param traderID ID for trader to send response to.
     * @param msg      Message to send to client.
     */
    public void sendResponse(String traderID, String msg) {
        broadcaster.sendCustomMessage(traderID, msg);
    }
}
