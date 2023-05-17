package nl.rug.aoop.stocksapplication.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.StockPortfolio;
import nl.rug.aoop.util.trader.Trader;
import nl.rug.aoop.stocksapplication.broadcast.Broadcaster;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Tests the resolve order command.
 */
@Slf4j
class ResolveOrdersCommandTest {
    private ResolveOrdersCommand roc;
    private final ProcessOrderCommand poc = Mockito.mock(ProcessOrderCommand.class);
    private Map<String, Stock> stocks;
    private final Broadcaster broadcaster = Mockito.mock(Broadcaster.class);

    private Trader buyer;
    private Trader seller;

    private Map<String, Object> getOptionsMap(Order buyOrder, Order sellOrder) {
        Map<String, Object> options = new HashMap<>();
        options.put("sellOrder", sellOrder);
        options.put("buyOrder", buyOrder);
        return options;
    }

    private Map<String, String> notificationJsonToMap(String msg) {
        log.info(msg);
        Gson gson = new Gson();
        return gson.fromJson(msg, Map.class);
    }

    @BeforeEach
    void setUp() {
        StockPortfolio buyerPortfolio = new StockPortfolio();
        StockPortfolio sellerPortfolio = new StockPortfolio();
        sellerPortfolio.addShares("stock", 20); // Add 20 shares of "stock" to seller

        buyer = new Trader("BUYER", "BUYER", 100, buyerPortfolio);
        seller = new Trader("SELLER", "SELLER", 100, sellerPortfolio);

        Map<String, Trader> traders = new HashMap<>();
        traders.put(buyer.getId(), buyer);
        traders.put(seller.getId(), seller);

        stocks = new HashMap<>();
        Stock stock = new Stock("stock", "stock", Long.valueOf(1000), 2.0);
        stocks.put(stock.getSymbol(), stock);

        roc = new ResolveOrdersCommand(poc, traders, stocks, broadcaster);
    }

    @Test
    void testOrdersFullyResolved() {
        Order buyorder = new Order(OrderType.BUY, "BUYER", "stock", 10.0, 5);
        Order sellorder = new Order(OrderType.SELL, "SELLER", "stock", 9.0, 5);

        roc.execute(getOptionsMap(buyorder, sellorder));

        // Checks that the price of the stock was updated.
        assertEquals(sellorder.getPrice(), stocks.get(sellorder.getStock()).getPrice());

        Mockito.verifyNoInteractions(poc);

        ArgumentCaptor<String> buyNotification = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> sellNotification = ArgumentCaptor.forClass(String.class);

        Mockito.verify(broadcaster).sendCustomMessage(eq(buyorder.getTraderID()), buyNotification.capture());
        Mockito.verify(broadcaster).sendCustomMessage(eq(sellorder.getTraderID()), sellNotification.capture());

        Map<String, String> buyNotificationMap = notificationJsonToMap(buyNotification.getValue());
        Map<String, String> sellNotificationMap = notificationJsonToMap(sellNotification.getValue());

        assertEquals(buyNotificationMap.get("message"), "Order Resolved!");
        assertEquals(sellNotificationMap.get("message"), "Order Resolved!");
    }

    @Test
    void testBuyOrdersPartiallyResolved() {
        Order buyorder = new Order(OrderType.BUY, "BUYER", "stock", 10.0, 10);
        Order sellorder = new Order(OrderType.SELL, "SELLER", "stock", 9.0, 5);

        roc.execute(getOptionsMap(buyorder, sellorder));

        ArgumentCaptor<Map> map = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(poc).execute(map.capture());
        assertTrue(map.getValue().containsKey("value"));

        Order remainingOrder = (Order) Command.retrieveOption("value", Order.class, map.getValue());
        log.info("Remaining Order: " + remainingOrder);

        assertEquals(5, remainingOrder.getNumShares());
        assertEquals(buyorder.getType(), remainingOrder.getType());
        assertEquals(buyorder.getPrice(), remainingOrder.getPrice());
        assertEquals(buyer.getId(), remainingOrder.getTraderID());

        // Checks that the price of the stock was updated.
        assertEquals(sellorder.getPrice(), stocks.get(remainingOrder.getStock()).getPrice());

        ArgumentCaptor<String> buyNotification = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> sellNotification = ArgumentCaptor.forClass(String.class);

        Mockito.verify(broadcaster).sendCustomMessage(eq(buyorder.getTraderID()), buyNotification.capture());
        Mockito.verify(broadcaster).sendCustomMessage(eq(sellorder.getTraderID()), sellNotification.capture());

        Map<String, String> buyNotificationMap = notificationJsonToMap(buyNotification.getValue());
        Map<String, String> sellNotificationMap = notificationJsonToMap(sellNotification.getValue());

        assertEquals(buyNotificationMap.get("message"), "Order Partially Resolved!");
        assertEquals(sellNotificationMap.get("message"), "Order Resolved!");
    }

    @Test
    void testFailOnNotEnoughSharesToSell() {
        Order buyOrder = new Order(OrderType.BUY, "BUYER", "stock", 10.0, 5);
        Order sellOrder = new Order(OrderType.SELL, "SELLER", "stock", 9.0, 21);

        roc.execute(getOptionsMap(buyOrder, sellOrder));

        // Checks that a message was never sent to the buyer.
        Mockito.verify(broadcaster, Mockito.times(0)).sendCustomMessage(eq(buyOrder.getTraderID()), any(String.class));

        ArgumentCaptor<String> msg = ArgumentCaptor.forClass(String.class);
        Mockito.verify(broadcaster).sendCustomMessage(eq(sellOrder.getTraderID()), msg.capture());
        Gson gson = new Gson();

        Map<String, String> notificationMap = gson.fromJson(msg.getValue(), Map.class);
        assertEquals("Failed to resolve order! Not enough stocks to sell!", notificationMap.get("message"));

        Mockito.verify(poc).execute(any(Map.class));
    }

    @Test
    void testFailOnNotEnoughFundsToBuy() {
        Order buyOrder = new Order(OrderType.BUY, "BUYER", "stock", 10.0, 20);
        Order sellOrder = new Order(OrderType.SELL, "SELLER", "stock", 10.0, 20);

        roc.execute(getOptionsMap(buyOrder, sellOrder));

        // Checks that a message was never sent to the seller.
        Mockito.verify(broadcaster, Mockito.times(0)).sendCustomMessage(eq(sellOrder.getTraderID()), any(String.class));

        ArgumentCaptor<String> msg = ArgumentCaptor.forClass(String.class);
        Mockito.verify(broadcaster).sendCustomMessage(eq(buyOrder.getTraderID()), msg.capture());

        Map<String, String> notificationMap = notificationJsonToMap(msg.getValue());
        assertEquals("Failed to resolve order! Not enough funds!", notificationMap.get("message"));

        Mockito.verify(poc).execute(any(Map.class));
    }

}