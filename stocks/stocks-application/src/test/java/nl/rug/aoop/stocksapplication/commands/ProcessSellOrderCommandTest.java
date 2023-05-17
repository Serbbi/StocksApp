package nl.rug.aoop.stocksapplication.commands;

import nl.rug.aoop.command.Command;
import nl.rug.aoop.stocksapplication.order.BuyOrderQueue;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;
import nl.rug.aoop.stocksapplication.order.SellOrderQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static nl.rug.aoop.stocksapplication.commands.OrderCommandTestUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

class ProcessSellOrderCommandTest {
    Command resolveOrdersCommand;

    @BeforeEach
    void setUp() {
        resolveOrdersCommand = Mockito.mock(Command.class);
    }

    @Test
    void testNoBuyOrders() {
        BuyOrderQueue buyOrderQueue = getMockBuyOrderQueue(null);
        SellOrderQueue sellOrderQueue = getMockSellOrderQueue(null);

        Command command = new ProcessSellOrderCommand(resolveOrdersCommand, buyOrderQueue, sellOrderQueue);

        Order order = new Order(OrderType.SELL, "RANDOMID", "STOCK", 1.0, 1);
        command.execute(getOptionsMap(order));

        Mockito.verify(sellOrderQueue).enqueue(order);
    }

    @Test
    void testNoSuitableBuyOrder() {
        SellOrderQueue sellOrderQueue = getMockSellOrderQueue(null);

        Order buyOrder = new Order(OrderType.BUY, "ID", "STOCK", 0.5, 1);
        BuyOrderQueue buyOrderQueue = getMockBuyOrderQueue(buyOrder);

        Command command = new ProcessSellOrderCommand(resolveOrdersCommand, buyOrderQueue, sellOrderQueue);

        Order order = new Order(OrderType.SELL, "TRADERID", "STOCK", 1.0, 1);
        command.execute(getOptionsMap(order));

        Mockito.verify(sellOrderQueue).enqueue(order);
    }

    @Test
    void testBuyOrderWithSamePrice() {
        SellOrderQueue sellOrderQueue = getMockSellOrderQueue(null);

        Order buyOrder = new Order(OrderType.BUY, "IDI", "STOCK", 1.0, 1);
        BuyOrderQueue buyOrderQueue = getMockBuyOrderQueue(buyOrder);

        Command command = new ProcessSellOrderCommand(resolveOrdersCommand, buyOrderQueue, sellOrderQueue);

        Order order = new Order(OrderType.SELL, "IDSELL", "STOCK", 1.0, 1);
        command.execute(getOptionsMap(order));

        Mockito.verify(resolveOrdersCommand).execute(any());

        Mockito.verify(sellOrderQueue, never()).enqueue(order);
    }

    @Test
    void testBuyOrderWithHigherPrice() {
        SellOrderQueue sellOrderQueue = getMockSellOrderQueue(null);

        Order buyOrder = new Order(OrderType.BUY, "IDK", "STOCK", 2.0, 1);
        BuyOrderQueue buyOrderQueue = getMockBuyOrderQueue(buyOrder);

        Command command = new ProcessSellOrderCommand(resolveOrdersCommand, buyOrderQueue, sellOrderQueue);

        Order order = new Order(OrderType.SELL, "IDC", "STOCK", 1.0, 1);
        command.execute(getOptionsMap(order));

        Mockito.verify(resolveOrdersCommand).execute(any());

        Mockito.verify(sellOrderQueue, never()).enqueue(order);
    }
}