package nl.rug.aoop.tradersapplication.bot;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.NetworkMessage;
import nl.rug.aoop.networking.client.Client;
import nl.rug.aoop.tradersapplication.command.StocksClientCommandHandlerBuilder;
import nl.rug.aoop.tradersapplication.ordergeneration.OrderGenerator;
import nl.rug.aoop.tradersapplication.ordergeneration.RandomOrderGenerator;
import nl.rug.aoop.util.order.Order;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.Trader;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.String.format;

/**
 * Thread that runs the automatic order generation.
 */
@Slf4j
public class OrderBot implements Bot {
    @Getter
    private final Trader trader;
    @Getter
    private final Client client;
    @Getter
    private Boolean running;
    private final Map<String, Stock> stocksMap;

    private OrderGenerator orderGenerator;

    /**
     * Main constructor.
     *
     * @param serverAddress Address of the server to connect to.
     * @param trader        Trader which this bot is.
     * @throws IOException Thrown by client.
     */
    public OrderBot(InetSocketAddress serverAddress, Trader trader) throws IOException {
        this.trader = trader;
        this.stocksMap = new ConcurrentHashMap<>();

        // Building command handler.
        StocksClientCommandHandlerBuilder builder = new StocksClientCommandHandlerBuilder();
        builder.addCommands(stocksMap, trader);
        CommandHandler commandHandler = builder.build();

        this.client = new Client(serverAddress, new BotMessageHandler(commandHandler));

        this.running = true;
    }

    /**
     * Sends the login message with their id to the server.
     */
    @Override
    public void sendLoginMessage() {
        client.sendMessage("login: " + trader.getId());
    }

    @Override
    public void run() {
        log.info(format("OrderBot (%s) running!", trader.getName()));

        sendLoginMessage();

        orderGenerator = new RandomOrderGenerator(stocksMap, trader);

        while (running) {
            Integer wait = ThreadLocalRandom.current().nextInt(1, 51);

            try {
                Thread.sleep(wait * 100);
            } catch (InterruptedException e) {
                log.error("Failed to sleep.");
                terminate();
                throw new RuntimeException(e);
            }
            log.info(format("(%s) Generating Order.", trader.getName()));
            Order order = orderGenerator.generateOrder();
            if (order != null) {
                log.info(format("(%s) Order Generated.", trader.getName()));
                sendOrder(order);
            }
        }
    }

    /**
     * Stops the bot.
     */
    public void terminate() {
        running = false;
    }

    /**
     * Sends an order to the server.
     *
     * @param order Order to be sent.
     */
    private void sendOrder(Order order) {
        Message message = new Message("processOrder", order.toJson());
        String msgString = message.toJSON();

        NetworkMessage netMsg = new NetworkMessage("MqPut", msgString);
        client.sendMessage(netMsg.toJSON());
    }
}
