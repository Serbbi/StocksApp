package nl.rug.aoop.stocksapplication.commands;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.messagequeue.commands.MqPutCommand;
import nl.rug.aoop.messagequeue.message.MessageQueue;
import nl.rug.aoop.command.CommandHandlerBuilder;
import nl.rug.aoop.util.stock.Stock;
import nl.rug.aoop.util.trader.Trader;
import nl.rug.aoop.stocksapplication.broadcast.Broadcaster;
import nl.rug.aoop.stocksapplication.order.BuyOrderQueue;
import nl.rug.aoop.stocksapplication.order.SellOrderQueue;

import java.util.Map;

/**
 * Creates the command handler.
 */
@Slf4j
public class StocksCommandHandlerBuilder extends CommandHandlerBuilder {

    /**
     * Initializes the command handler.
     */
    public StocksCommandHandlerBuilder() {
        this.reset();
    }

    /**
     * Adds all the commands which involve processing orders.
     *
     * @param traderMap   Hash Map that holds all the traders in the system.
     * @param stocksMap   Hash Map that holds all the stocks in the system.
     * @param boq         The buy order queue.
     * @param soq         The sell order queue.
     * @param broadcaster the broadcaster.
     */
    public void addOrderCommands(Map<String, Trader> traderMap, Map<String, Stock> stocksMap,
                                 BuyOrderQueue boq, SellOrderQueue soq, Broadcaster broadcaster) {
        Command processOrderCommand = new ProcessOrderCommand(getCommandHandler(), traderMap, stocksMap);

        Command resolveOrdersCommand = new ResolveOrdersCommand(processOrderCommand, traderMap, stocksMap, broadcaster);

        Command processBuyOrderCommand = new ProcessBuyOrderCommand(resolveOrdersCommand, boq, soq);
        Command processSellOrderCommand = new ProcessSellOrderCommand(resolveOrdersCommand, boq, soq);

        addCommand("processOrder", processOrderCommand);
        addCommand("processBuyOrder", processBuyOrderCommand);
        addCommand("processSellOrder", processSellOrderCommand);

    }

    /**
     * Adds the commands for interacting with the message queue.
     *
     * @param mq The message queue that will be accessed by the commands.
     */
    public void addMQCommands(MessageQueue mq) {
        addCommand("MqPut", new MqPutCommand(mq));
    }

    /**
     * Adds the command for logging in the app.
     *
     * @param tradersMap the trader map for the new trader to be added in.
     */
    public void addLogInCommands(Map<String, Trader> tradersMap) {
        addCommand("LogIn", new LogInCommand(tradersMap));
    }
}
