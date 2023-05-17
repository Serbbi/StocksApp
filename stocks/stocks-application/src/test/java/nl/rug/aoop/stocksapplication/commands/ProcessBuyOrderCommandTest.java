package nl.rug.aoop.stocksapplication.commands;

import nl.rug.aoop.command.Command;
import nl.rug.aoop.stocksapplication.order.*;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static nl.rug.aoop.stocksapplication.commands.OrderCommandTestUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

class ProcessBuyOrderCommandTest {
    Command resolveOrdersCommand;

    @BeforeEach
    void setUp() {
        resolveOrdersCommand = Mockito.mock(Command.class);
//        Mockito.when(resolveOrdersCommand.execute(any()));
    }

    @Test
    void testNoSellOrders() {
        BuyOrderQueue buyOrderQueue = getMockBuyOrderQueue(null);
        SellOrderQueue sellOrderQueue = getMockSellOrderQueue(null);

        Command command = new ProcessBuyOrderCommand(resolveOrdersCommand, buyOrderQueue, sellOrderQueue);

        Order order = new Order(OrderType.BUY, "randomID", "STOCK", 1.0, 1);
        command.execute(getOptionsMap(order));

        Mockito.verify(buyOrderQueue).enqueue(order);
    }

    @Test
    void testNoSuitableSellOrder() {
        BuyOrderQueue buyOrderQueue = getMockBuyOrderQueue(null);

        Order sellOrder = new Order(OrderType.SELL, "randomID", "STOCK", 2.0, 1);
        SellOrderQueue sellOrderQueue = getMockSellOrderQueue(sellOrder);

        Command command = new ProcessBuyOrderCommand(resolveOrdersCommand, buyOrderQueue, sellOrderQueue);

        Order order = new Order(OrderType.BUY, "RANDOMID", "STOCK", 1.0, 1);
        command.execute(getOptionsMap(order));

        Mockito.verify(buyOrderQueue).enqueue(order);
    }

    @Test
    void testSellOrderWithSamePrice() {
        BuyOrderQueue buyOrderQueue = getMockBuyOrderQueue(null);

        Order sellOrder = new Order(OrderType.BUY, "RENDOMID", "STOCK", 1.0, 1);
        SellOrderQueue sellOrderQueue = getMockSellOrderQueue(sellOrder);

        Command command = new ProcessBuyOrderCommand(resolveOrdersCommand, buyOrderQueue, sellOrderQueue);

        Order order = new Order(OrderType.BUY, "IDRANDOM", "STOCK", 1.0, 1);
        command.execute(getOptionsMap(order));

        Mockito.verify(resolveOrdersCommand).execute(any());

        Mockito.verify(buyOrderQueue, never()).enqueue(order);
    }

    @Test
    void testSellOrderWithLowerPrice() {
        BuyOrderQueue buyOrderQueue = getMockBuyOrderQueue(null);

        Order sellOrder = new Order(OrderType.BUY, "WANNABEID", "STOCK", 0.5, 1);
        SellOrderQueue sellOrderQueue = getMockSellOrderQueue(sellOrder);

        Command command = new ProcessBuyOrderCommand(resolveOrdersCommand, buyOrderQueue, sellOrderQueue);

        Order order = new Order(OrderType.BUY, "IDIDID", "STOCK", 1.0, 1);
        Map<String, Object> map = getOptionsMap(order);
        command.execute(map);

        Mockito.verify(resolveOrdersCommand).execute(any());

        Mockito.verify(buyOrderQueue, never()).enqueue(order);
    }

}