package nl.rug.aoop.command;

import java.util.Map;

/**
 * Interface for implementing the Command pattern.
 * Defines the execute behaviour which will perform commands.
 */
public interface Command {

    /**
     * Implements the action that should be taken when the command is run.
     *
     * @param options Map that holds extra data for the command.
     */
    void execute(Map<String, Object> options);

    /**
     * Gets a parameter out of the options map, and casts it to the appropriate class type.
     *
     * @param key     Key of parameter in map.
     * @param E       Class which option should be an instance of.
     * @param options Options map.
     * @param <E>     Class which option should be an instance of.
     * @return Object of type E.
     * @throws IllegalArgumentException If the type of the option parameter is not an instance of class E.
     */
    static <E> E retrieveOption(String key, Class E, Map<String, Object> options) throws IllegalArgumentException {
        Object obj = options.get(key);
        if (E.isInstance(obj)) {
            return (E) obj;
        }
        throw new IllegalArgumentException("Incorrect option type. Expected " + E.getName() +
                " but got " + obj.getClass().getName() + ".");
    }
}
