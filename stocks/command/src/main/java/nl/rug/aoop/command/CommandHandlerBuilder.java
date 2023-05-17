package nl.rug.aoop.command;

/**
 * Abstract class for building command handlers.
 */
public abstract class CommandHandlerBuilder {
    private CommandHandler commandHandler = new CommandHandler();

    /**
     * Adds a command to the command hander.
     *
     * @param key Key of the command.
     * @param c   The command to be added to the command hander.
     */
    protected void addCommand(String key, Command c) {
        this.commandHandler.addCommand(key, c);
    }

    /**
     * Returns the built command handler, and resets the command handler so that another command handler can
     * be created if required.
     *
     * @return The command handler with the commands that were added.
     */
    public CommandHandler build() {
        CommandHandler ch = this.commandHandler;
        reset();
        return ch;
    }

    /**
     * Resets the command handler.
     */
    public void reset() {
        this.commandHandler = new CommandHandler();
    }

    /**
     * Returns the command handler.
     *
     * @return Command handler.
     */
    protected CommandHandler getCommandHandler() {
        return this.commandHandler;
    }
}
