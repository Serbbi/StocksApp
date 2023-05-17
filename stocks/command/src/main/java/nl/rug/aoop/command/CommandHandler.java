package nl.rug.aoop.command;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the execution of commands.
 */
@Slf4j
public class CommandHandler {

    private static final String ERROR_RESPONSE = "Error: Command not found!";
    private final Map<String, Command> commands;

    /**
     * Initialises the commands map.
     */
    public CommandHandler() {
        commands = new HashMap<>();
    }

    /**
     * Executes the requested command.
     * If key does not exist, the ERROR_RESPONSE is sent back to the client.
     *
     * @param key     String key for the command that should be called.
     * @param options Map that holds all the extra option values which might be needed by the commmand.
     */
    public void executeCommand(String key, Map<String, Object> options) {
        log.info("Executing command: " + key);
        if (commands.containsKey(key)) {
            Command command = commands.get(key);

            command.execute(options);
        } else {
            log.error(ERROR_RESPONSE);
        }
    }

    /**
     * Adds a command object to the commands map.
     *
     * @param key     Key which can be used to call the command.
     * @param command Command instance to be added to map.
     */
    public void addCommand(String key, Command command) {
        commands.put(key, command);
    }

    /**
     * Returns the standard error response for when a command isn't recognised.
     *
     * @return ERROR_RESPONSE
     */
    public static String getErrorResponse() {
        return ERROR_RESPONSE;
    }

    @Override
    public String toString() {
        String commandStr = "";
        for (String key : this.commands.keySet()) {
            commandStr += key + ",";
        }
        return "Commands: " + commandStr + "";
    }
}
