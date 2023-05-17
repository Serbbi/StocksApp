package nl.rug.aoop.stocksapplication.commands;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.MessageQueue;
import nl.rug.aoop.stocksapplication.broadcast.Broadcaster;
import nl.rug.aoop.stocksapplication.order.BuyOrderQueue;
import nl.rug.aoop.stocksapplication.order.SellOrderQueue;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.Trader;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@Slf4j
class StocksCommandHandlerBuilderTest {

    private Map<String, Object> createSingletonMap(String key, Object obj) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, obj);
        return map;
    }

    @Test
    void testBuild() {
        StocksCommandHandlerBuilder builder = new StocksCommandHandlerBuilder();
        CommandHandler cmd = builder.build();
        assertNotNull(cmd);
    }

    @Test
    void testBuildCommandHandlerWithMQCommands() {
        MessageQueue mq = Mockito.mock(MessageQueue.class);

        StocksCommandHandlerBuilder builder = new StocksCommandHandlerBuilder();
        builder.addMQCommands(mq);
        CommandHandler cmd = builder.build();
        String msg = new Message("header", "value").toJSON();

        cmd.executeCommand("MqPut", createSingletonMap("value", msg));

        Mockito.verify(mq).enqueue(any(Message.class));
    }

    @Test
    void testBuildCommandHandlerWithLogInCommands() {
        Map<String, Trader> tradersMap = Mockito.mock(Map.class);

        StocksCommandHandlerBuilder builder = new StocksCommandHandlerBuilder();
        builder.addLogInCommands(tradersMap);
        CommandHandler cmd = builder.build();

        Trader trader = Mockito.mock(Trader.class);
        Mockito.when(trader.getId()).thenReturn("bot1");

        cmd.executeCommand("LogIn", createSingletonMap("trader", trader));

        Mockito.verify(tradersMap).put(eq(trader.getId()), eq(trader));
    }

    @Test
    void testBuildCommandHandlerWithOrderCommands() {
        Map<String, Trader> traderMap = Mockito.mock(Map.class);
        Map<String, Stock> stocksMap = Mockito.mock(Map.class);
        BuyOrderQueue boq = Mockito.mock(BuyOrderQueue.class);
        SellOrderQueue soq = Mockito.mock(SellOrderQueue.class);
        Broadcaster broadcaster = Mockito.mock(Broadcaster.class);

        StocksCommandHandlerBuilder builder = new StocksCommandHandlerBuilder();
        builder.addOrderCommands(traderMap, stocksMap, boq, soq, broadcaster);
        CommandHandler cmd = builder.build();
        log.info(cmd.toString());

        // Avoid very long test that is out of the scope of this test class.
        assertEquals(cmd.toString(), "Commands: processOrder,processSellOrder,processBuyOrder,");


    }

}