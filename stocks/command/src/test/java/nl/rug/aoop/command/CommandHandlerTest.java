package nl.rug.aoop.command;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;

class CommandHandlerTest {

    @Test
    void addAndRunCommand() {
        CommandHandler commandHandler = new CommandHandler();

        Command command = Mockito.mock(Command.class);

        String key = "command";
        HashMap<String, Object> map = new HashMap<>();

        commandHandler.addCommand(key, command);

        commandHandler.executeCommand(key, map);

        Mockito.verify(command).execute(map);

    }
}