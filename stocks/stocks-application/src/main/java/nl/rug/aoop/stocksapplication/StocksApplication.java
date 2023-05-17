package nl.rug.aoop.stocksapplication;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.initialization.WebViewFactory;
import nl.rug.aoop.messagequeue.consumer.MQConsumer;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.ThreadSafeOrderedMessageQueue;
import nl.rug.aoop.messagequeue.messageHandlers.MQMessageHandler;
import nl.rug.aoop.model.StockDataModel;
import nl.rug.aoop.model.StockExchangeDataModel;
import nl.rug.aoop.model.TraderDataModel;
import nl.rug.aoop.networking.client.messagehandlers.MessageHandler;
import nl.rug.aoop.networking.server.Server;
import nl.rug.aoop.stocksapplication.broadcast.Broadcaster;
import nl.rug.aoop.stocksapplication.commands.*;
import nl.rug.aoop.stocksapplication.order.BuyOrderQueue;
import nl.rug.aoop.stocksapplication.order.SellOrderQueue;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.stock.StocksLoader;
import nl.rug.aoop.util.trader.Trader;
import nl.rug.aoop.util.trader.TradersLoader;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The top level class for the entire StocksApplication.
 */
@Slf4j
public class StocksApplication implements MQConsumer, Runnable, StockExchangeDataModel {
    private boolean running;
    private final Server server;
    private final CommandHandler commandHandler;
    private final ThreadSafeOrderedMessageQueue messageQueue;
    private final BuyOrderQueue buyOrderQueue;
    private final SellOrderQueue sellOrderQueue;
    private final Map<String, Trader> tradersMap;
    private final Map<String, Stock> stocksMap;
    private final Map<String, UUID> clientHandlerTradersMap;
    private Broadcaster broadcaster;
    // View Fields:
    private final Map<Integer, String> indexToStockKey;
    private final Map<Integer, String> indexToTraderKey;

    private final ExecutorService executorService;

    /**
     * Constructs the StocksApplication.
     *
     * @param port The port which the server should use.
     */
    public StocksApplication(Integer port) {
        executorService = Executors.newCachedThreadPool();
        indexToStockKey = new HashMap<>();
        indexToTraderKey = new HashMap<>();
        buyOrderQueue = new BuyOrderQueue();
        sellOrderQueue = new SellOrderQueue();
        clientHandlerTradersMap = new ConcurrentHashMap<>();

        tradersMap = TradersLoader.getMapFromYaml("traders");
        stocksMap = StocksLoader.getMapFromYaml("stocks");
        mapIndexes();

        messageQueue = new ThreadSafeOrderedMessageQueue();
        broadcaster = new Broadcaster();

        // Building the command handler.
        StocksCommandHandlerBuilder chf = new StocksCommandHandlerBuilder();
        chf.addOrderCommands(tradersMap, stocksMap, buyOrderQueue, sellOrderQueue, broadcaster);
        chf.addMQCommands(messageQueue);
        chf.addLogInCommands(tradersMap);
        commandHandler = chf.build();
        try {
            MessageHandler mqMessageHandler = new MQMessageHandler(commandHandler);
            server = new Server(mqMessageHandler, port, new LoginHandler(clientHandlerTradersMap));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        broadcaster.setAll(stocksMap, tradersMap, server, clientHandlerTradersMap);
        startView();
    }

    private void startView() {
        WebViewFactory simpleViewFactory = new WebViewFactory();
        simpleViewFactory.createView(this);
    }

    private void mapIndexes() {
        List<String> list = stocksMap.keySet().stream().toList();
        for (int i = 0; i < stocksMap.size(); i++) {
            indexToStockKey.put(i, list.get(i));
        }
        list = tradersMap.keySet().stream().toList();
        for (int i = 0; i < tradersMap.size(); i++) {
            indexToTraderKey.put(i, list.get(i));
        }
    }

    @Override
    public Message poll() {
        return messageQueue.dequeue();
    }

    @Override
    public void run() {
        running = true;

        logStartMessage();

        executorService.submit(server);
        executorService.submit(broadcaster);

        // Create a loop that infinitely polls the message queue.
        while (running) {
            Message message = poll();

            if (message == null) {
                continue;
            }

            Map<String, Object> options = new HashMap<>();
            options.put("value", message.getValue());

            commandHandler.executeCommand(message.getHeader(), options);
        }
    }

    /**
     * Terminates the thread.
     */
    public void terminate() {
        executorService.shutdownNow();
        this.running = false;
    }

    @Override
    public StockDataModel getStockByIndex(int index) {
        return stocksMap.get(indexToStockKey.get(index));
    }

    @Override
    public int getNumberOfStocks() {
        return stocksMap.size();
    }

    @Override
    public TraderDataModel getTraderByIndex(int index) {
        return tradersMap.get(indexToTraderKey.get(index));
    }

    @Override
    public int getNumberOfTraders() {
        return tradersMap.size();
    }

    /**
     * Outputs a fancy startup message. Just some fun :).
     */
    public void logStartMessage() {
        log.info("\n" +
                " __    __     _                            _        \n" +
                "/ / /\\ \\ \\___| | ___ ___  _ __ ___   ___  | |_ ___  \n" +
                "\\ \\/  \\/ / _ \\ |/ __/ _ \\| '_ ` _ \\ / _ \\ | __/ _ \\ \n" +
                " \\  /\\  /  __/ | (_| (_) | | | | | |  __/ | || (_) |\n" +
                "  \\/  \\/ \\___|_|\\___\\___/|_| |_| |_|\\___|  \\__\\___/ \n" +
                "                                                    \n" +
                " __ _              _          __                           _ \n" +
                "/ _\\ |_ ___  _ __ | | _____  / _\\ ___ _ ____   _____ _ __ / \\\n" +
                "\\ \\| __/ _ \\| '_ \\| |/ / __| \\ \\ / _ \\ '__\\ \\ / / _ \\ '__/  /\n" +
                "_\\ \\ || (_) | | | |   <\\__ \\ _\\ \\  __/ |   \\ V /  __/ | /\\_/ \n" +
                "\\__/\\__\\___/|_| |_|_|\\_\\___/ \\__/\\___|_|    \\_/ \\___|_| \\/   ");
    }
}
