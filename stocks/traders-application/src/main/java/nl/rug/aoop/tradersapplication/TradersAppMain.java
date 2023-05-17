package nl.rug.aoop.tradersapplication;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.util.trader.Trader;
import nl.rug.aoop.util.trader.TradersLoader;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Runs the traders application.
 */
@Slf4j
public class TradersAppMain {
    /**
     * Starts up the traders application, and it's GUI.
     *
     * @param args Standard arguments.
     */
    public static void main(String[] args) {
        log.info("Starting Traders Application!");
        String prt = System.getenv("STOCK_EXCHANGE_PORT");
        String host = System.getenv("STOCK_EXCHANGE_HOST");
        if (prt == null) {
            log.warn("No port specified. Using default port: 8080");
        }

        if (host == null) {
            log.warn("No host specified. Using default host: localhost");
        }

        Integer port = (prt == null) ? 8081 : Integer.parseInt(prt);
        host = (host == null) ? "localhost" : host;

        InetSocketAddress serverAddress = new InetSocketAddress(host, port);

        Map<String, Trader> tradersMap = TradersLoader.getMapFromYaml("traders");
        TradersApplication tradersApplication = new TradersApplication(serverAddress, tradersMap);
        tradersApplication.run();
    }
}