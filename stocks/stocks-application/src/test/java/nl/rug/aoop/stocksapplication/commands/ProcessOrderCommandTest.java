package nl.rug.aoop.stocksapplication.commands;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.Trader;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.order.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;

@Slf4j
class ProcessOrderCommandTest {

    CommandHandler handler;
    Command processOrderCommand;
    Command processBuyOrderCommand;
    Command processSellOrderCommand;

    Map<String, Trader> traderMap;
    Map<String, Stock> stocksMap;

    Trader getMockTrader(boolean hasSufficientFunds, boolean hasSuffientShares) {
        String id = "traderID";
        Trader trader = Mockito.mock(Trader.class);
        Mockito.when(trader.getId()).thenReturn(id);
        Mockito.when(trader.checkSufficientFundsForPurchase(anyDouble(), anyInt())).thenReturn(hasSufficientFunds);
        Mockito.when(trader.checkSufficientSharesForSale(any(Stock.class), anyInt())).thenReturn(hasSuffientShares);
        return trader;
    }

    Stock getMockStock() {
        Stock stock = Mockito.mock(Stock.class);
        Mockito.when(stock.getSymbol()).thenReturn("STOCK");
        return stock;
    }

    Map<String, Object> getOptionsMap(Trader trader, OrderType orderType) {
        Stock stock = getMockStock();
        stocksMap.put(stock.getSymbol(), stock);

        Order order = new Order(orderType, trader.getId(), stock.getSymbol(), 1.0, 1);
        Map<String, Object> options = new HashMap<>();
        options.put("value", order);
        return options;
    }

    @BeforeEach
    void createHandler() {
        handler = new CommandHandler();
        processBuyOrderCommand = Mockito.mock(Command.class);
        processSellOrderCommand = Mockito.mock(Command.class);

        handler.addCommand("processBuyOrder", processBuyOrderCommand);
        handler.addCommand("processSellOrder", processSellOrderCommand);

        traderMap = new HashMap<>();
        stocksMap = new HashMap<>();
        processOrderCommand = new ProcessOrderCommand(handler, traderMap, stocksMap);
    }

    /**
     * Verifies that if a buy order passed, then the ProcessBuyOrderCommand is run.
     */
    @Test
    public void testProcessBuyOrderCommand() {
        Trader trader = getMockTrader(true, true);
        traderMap.put(trader.getId(), trader);

        Map<String, Object> options = getOptionsMap(trader, OrderType.BUY);

        processOrderCommand.execute(options);

        ArgumentCaptor<Map> argMap = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(processBuyOrderCommand).execute(argMap.capture());
        Mockito.verify(processSellOrderCommand, Mockito.never()).execute(options);

        assertEquals(argMap.getValue().get("order"), options.get("value"));
    }

    /**
     * Verifies that if a sell order passed, then the ProcessSellOrderCommand is run.
     */
    @Test
    public void testProcessSellOrderCommand() {
        Trader trader = getMockTrader(true, true);
        traderMap.put(trader.getId(), trader);

        Map<String, Object> options = getOptionsMap(trader, OrderType.SELL);

        processOrderCommand.execute(options);

        ArgumentCaptor<Map> argMap = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(processSellOrderCommand).execute(argMap.capture());
        Mockito.verify(processBuyOrderCommand, Mockito.never()).execute(options);

        assertEquals(argMap.getValue().get("order"), options.get("value"));
    }

    /**
     * Verifies that if the trader doesn't have enough money, the order is rejected.
     */
    @Test
    public void testInsufficientFunds() {
        Trader trader = getMockTrader(false, true);
        traderMap.put(trader.getId(), trader);

        Map<String, Object> options = getOptionsMap(trader, OrderType.BUY);

        processOrderCommand.execute(options);
        Mockito.verify(processSellOrderCommand, Mockito.never()).execute(options);
        Mockito.verify(processBuyOrderCommand, Mockito.never()).execute(options);
    }

    /**
     * Verifies that if the trader doesn't have enough money, the order is rejected.
     */
    @Test
    public void testInsufficientStocks() {
        Trader trader = getMockTrader(true, false);
        traderMap.put(trader.getId(), trader);

        Map<String, Object> options = getOptionsMap(trader, OrderType.SELL);

        processOrderCommand.execute(options);
        Mockito.verify(processSellOrderCommand, Mockito.never()).execute(options);
        Mockito.verify(processBuyOrderCommand, Mockito.never()).execute(options);
    }

}