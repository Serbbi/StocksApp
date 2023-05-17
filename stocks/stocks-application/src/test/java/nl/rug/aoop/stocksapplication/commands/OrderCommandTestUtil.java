package nl.rug.aoop.stocksapplication.commands;

import nl.rug.aoop.stocksapplication.order.BuyOrderQueue;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.stocksapplication.order.SellOrderQueue;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

public class OrderCommandTestUtil {
    public static Map<String, Object> getOptionsMap(Order order) {
        Map<String, Object> options = new HashMap<>();
        options.put("order", order);
        return options;
    }

    public static BuyOrderQueue getMockBuyOrderQueue(Order dequeueReturn) {
        BuyOrderQueue orderQueue = Mockito.mock(BuyOrderQueue.class);
        Mockito.when(orderQueue.dequeue(Mockito.anyString())).thenReturn(dequeueReturn);
        return orderQueue;
    }

    public static SellOrderQueue getMockSellOrderQueue(Order dequeueReturn) {
        SellOrderQueue orderQueue = Mockito.mock(SellOrderQueue.class);
        Mockito.when(orderQueue.dequeue(Mockito.anyString())).thenReturn(dequeueReturn);
        return orderQueue;
    }
}
