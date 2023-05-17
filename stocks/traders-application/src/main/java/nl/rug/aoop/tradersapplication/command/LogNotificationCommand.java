package nl.rug.aoop.tradersapplication.command;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.Command;
import nl.rug.aoop.util.trader.Trader;

import java.util.Map;

import static java.lang.String.format;

/**
 * Run when a notification is received from server.
 * Logs the notification to console.
 */
@Slf4j
public class LogNotificationCommand implements Command {
    private Trader trader;

    /**
     * Main Constructor.
     *
     * @param trader Trader who the notification is for.
     */
    public LogNotificationCommand(Trader trader) {
        this.trader = trader;
    }

    @Override
    public void execute(Map<String, Object> options) {
        String notification = Command.retrieveOption("value", String.class, options); //
        log.info(format("(%s) %s", trader.getName(), notification));
    }
}
