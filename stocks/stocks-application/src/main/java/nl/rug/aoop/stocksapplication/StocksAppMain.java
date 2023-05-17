package nl.rug.aoop.stocksapplication;

import lombok.extern.slf4j.Slf4j;

/**
 * Runs the stocks application.
 */
@Slf4j
public class StocksAppMain {
    /**
     * Main method for the stocks application.
     *
     * @param args Args for the app.
     */
    public static void main(String[] args) {
        String prt = System.getenv("STOCK_EXCHANGE_PORT");

        Integer port = (prt == null) ? 8081 : Integer.parseInt(prt);

        if (prt == null) {
            log.warn("No port specified. Using default port: 8081");
        }

        new StocksApplication(port).run();
    }
}
