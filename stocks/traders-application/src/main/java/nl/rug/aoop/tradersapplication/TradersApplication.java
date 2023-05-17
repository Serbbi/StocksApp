package nl.rug.aoop.tradersapplication;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.tradersapplication.bot.factory.BotFactory;
import nl.rug.aoop.tradersapplication.bot.factory.OrderBotFactory;
import nl.rug.aoop.util.trader.Trader;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static org.awaitility.Awaitility.await;

/**
 * Manages the bot threads.
 */
@Slf4j
public class TradersApplication implements Runnable {

    private final Map<String, Trader> tradersMap;
    private final Map<String, StocksClient> stocksClients;
    private final ExecutorService executorService;
    private Boolean running = false;

    /**
     * Main constructor. Creates all the bots.
     *
     * @param serverAddr Server address that all the bot clients should connect to.
     * @param tradersMap the traders map to assign a bot to each trader.
     */
    public TradersApplication(InetSocketAddress serverAddr, Map<String, Trader> tradersMap) {
        this.tradersMap = tradersMap;
        stocksClients = new HashMap<>();
        executorService = Executors.newCachedThreadPool();

        BotFactory factory = new OrderBotFactory(serverAddr);

        tradersMap.forEach((key, trader) -> {
            stocksClients.put(key, factory.createBot(trader));
        });
    }

    @Override
    public void run() {
        running = true;
        logStartupMessage();

        stocksClients.forEach((key, bot) -> {
            log.info(format("Starting bot (%s)", bot.getTrader().getName()));
            this.executorService.submit(bot.getClient());
            await().until(bot.getClient()::isRunning);

            this.executorService.submit(bot);
            await().until(bot::getRunning);
        });

        while (running) {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }
        }

        terminate();
    }

    /**
     * Outputs a fancy startup message. Just some fun :).
     */
    private void logStartupMessage() {
        log.info("\n" +
                " __    __     _                            _        \n" +
                "/ / /\\ \\ \\___| | ___ ___  _ __ ___   ___  | |_ ___  \n" +
                "\\ \\/  \\/ / _ \\ |/ __/ _ \\| '_ ` _ \\ / _ \\ | __/ _ \\ \n" +
                " \\  /\\  /  __/ | (_| (_) | | | | | |  __/ | || (_) |\n" +
                "  \\/  \\/ \\___|_|\\___\\___/|_| |_| |_|\\___|  \\__\\___/ \n" +
                "                                                    \n" +
                " __ _              _            ___ _ _            _     _ \n" +
                "/ _\\ |_ ___  _ __ | | _____    / __\\ (_) ___ _ __ | |_  / \\\n" +
                "\\ \\| __/ _ \\| '_ \\| |/ / __|  / /  | | |/ _ \\ '_ \\| __|/  /\n" +
                "_\\ \\ || (_) | | | |   <\\__ \\ / /___| | |  __/ | | | |_/\\_/ \n" +
                "\\__/\\__\\___/|_| |_|_|\\_\\___/ \\____/|_|_|\\___|_| |_|\\__\\/   ");
    }

    /**
     * Stops the traders app.
     */
    public void terminate() {
        running = false;
        stocksClients.forEach((key, bot) -> {
            bot.terminate();
        });
        executorService.shutdown();
    }
}
