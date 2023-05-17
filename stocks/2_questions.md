# Question 1

In the assignment, you had to create a `MessageHandler` interface. Please answer the following two questions:

1. Describe the benefits of using this `MessageHandler` interface. (~50-100 words)
2. Instead of creating an implementation of `MessageHandler` that invokes a command handler, we could also pass the
   command handler to the client/server directly without the middle man of the `MessageHandler` implementation. What are
   the implications of this? (~50-100 words)

___

**Answer**:

1. By using a `MessageHandler` interface we can separate the networking from the way messages are handled,
by the means of decoupling. So separating how the messages are handled can be done by creating a class to deal 
with this stuff and the before mentioned interface. Interfaces allow us to specify behaviour without specifying the
implementation, because we are interested in what needs to be done and not how it is done.

2. Basically you'll need to write all the message handling command execution in the server/client which would result in 
very ugly methods. There is no decoupling so the code which is responsible for messages will be in the same class as
the network and those two have nothing to do with each other. There is also no flexibility at all so adding or changing 
the ways we are handling the messages will be difficult. Maintainability and readability also suffer from this.

___

# Question 2

One of your colleagues wrote the following class:

```java
public class RookieImplementation {

    private final Car car;

    public RookieImplementation(Car car) {
        this.car = car;
    }

    public void carEventFired(String carEvent) {
        if ("steer.left".equals(carEvent)) {
            car.steerLeft();
        } else if ("steer.right".equals(carEvent)) {
            car.steerRight();
        } else if ("engine.start".equals(carEvent)) {
            car.startEngine();
        } else if ("engine.stop".equals(carEvent)) {
            car.stopEngine();
        } else if ("pedal.gas".equals(carEvent)) {
            car.accelerate();
        } else if ("pedal.brake".equals(carEvent)) {
            car.brake();
        }
    }
}
```

This code makes you angry. Briefly describe why it makes you angry and provide the improved code below.

___

**Answer**:

This code uses if statements in order to choose an action to execute based on a string parameter. This is very
unreadable. There are 2 possible ways to fix this. FThe first, simpler option is to use a way is to use a `switch`
statement. This makes the code far more readable as it removes the repeated `String` matching code. However, the best way to do it is to implement the command pattern.

In the code below I have implemented the command pattern. THis new implementation performs the same function as the code above.
Although the new code is a lot longer, it is much more readable and expandable. Firstly, the `Command` interface and
`CommandHandler` are general enough to allow them to be used for other commands, not just ones for the car class.
Furthermore, if we wanted to add more commands for cars, all we would have to do it create a new command that implements
the `Command` class, and add a line to the command factory that adds the given command to the handler. Furthermore, the
Command pattern decouples the details about the execution of a command from the instance upon which the command is being
run on.

Improved code:

```java
import java.util.HashMap;

// Command Interface
interface Command() {
    void execute(String command);
}

// Superclass for all car commands
class CarCommand implements Command {
    public Car car;

    public CarSteerLeftCommand(Car car) {
        this.car = car;
    }
}

// Car steer commands
class CarSteerLeftCommand extends CarCommand {
    public execute() {
        car.steerLeft();
    }
}

class CarSteerRightCommand extends CarCommand {
    public execute() {
        car.steerRight();
    }
}

// Car engine commands
class CarStartEngineCommand extends CarCommand {
    public execute() {
        car.startEngine();
    }
}

class CarStopEngineCommand extends CarCommand {
    public execute() {
        car.stopEngine();
    }
}

// Car pedal commands
class CarPedalGasCommand extends CarCommand {
    public execute() {
        car.accelerate();
    }
}

class CarPedalBreakCommand extends CarCommand {
    public execute() {
        car.accelerate();
    }
}

// Executes Commands
class CommandHandler {
    private Map<String, Command> commandMap;

    public CarCommandHandler() {
        this.commandMap = new HashMap<>();
    }

    public void executeCommand(String event) {
        if (commands.containsKey(key)) {
            Command command = commands.get(key);
        } else {
            throw new IllegalArgumentException("Command does not exist.");
        }
    }

    public void addCommand(String commandString, Command command) {
        this.commandMap.add(commandString, command);
    }
}

// Adds all the car commands to a command handler.
class CarCommandHandlerFactory() {

    Car car;

    public CarCommandHandlerFactory(Car car) {
        this.car = car;
    }

    public CommandHandler create() {
        CommandHandler handler = new CommandHandler();
        handler.addCommand("steer.left", new CarSteerLeftCommand(car));
        handler.addCommand("steer.right", new CarSteerRightCommand(car));
        handler.addCommand("engine.start", new CarStartEngineCommand(car));
        handler.addCommand("engine.stop", new CarStopEngineCommand(car));
        handler.addCommand("pedal.gas", new CarPedalGasCommand(car));
        handler.addCommand("pedal.break", new CarPedalBreakCommand(car));
        return handler;
    }

}

```

___

# Question 3

You have the following exchange with a colleague:

> **Colleague**: "Hey, look at this! It's super handy. Pretty simple to write custom experiments."

```java
class Experiments {
    public static Model runExperimentA(DataTable dt) {
        CommandHandler commandSequence = new CleanDataTableCommand()
                .setNext(new RemoveCorrelatedColumnsCommand())
                .setNext(new TrainSVMCommand());

        Config config = new Options();
        config.set("broadcast", true);
        config.set("svmdatatable", dt);

        commandSequence.handle(config);

        return (Model) config.get("svmmodel");
    }

    public static Model runExperimentB() {
        CommandHandler commandSequence = new CleanDataTableCommand()
                .setNext(new TrainSGDCommand());

        Config config = new Options();
        config.set("broadcast", true);
        config.set("sgddatatable", dt);

        commandSequence.handle(config);

        return (Model) config.get("sgdmodel");
    }
}
```

> **Colleague**: "I could even create this method to train any of the models we have. Do you know how Jane did it?"

```java
class Processor {
    public static Model getModel(String algorithm, DataTable dt) {
        CommandHandler commandSequence = new TrainSVMCommand()
                .setNext(new TrainSDGCommand())
                .setNext(new TrainRFCommand())
                .setNext(new TrainNNCommand());

        Config config = new Options();
        config.set("broadcast", false);
        config.set(algorithm + "datatable", dt);

        commandSequence.handle(config);

        return (Model) config.get(algorithm + "model");
    }
}
```

> **You**: "Sure! She is using the command pattern. Easy indeed."
>
> **Colleague**: "Yeah. But look again. There is more; she uses another pattern on top of it. I wonder how it works."

1. What is this other pattern? What advantage does it provide to the solution? (~50-100 words)

2. You know the code for `CommandHandler` has to be a simple abstract class in this case, probably containing four
   methods:

- `CommandHandler setNext(CommandHandler next)` (implemented in `CommandHandler`),
- `void handle(Config config)` (implemented in `CommandHandler`),
- `abstract boolean canHandle(Config config)`,
- `abstract void execute(Config config)`.

Please provide a minimum working example of the `CommandHandler` abstract class.

___

**Answer**:

1. Other pattern: (Abstract) Factory;
This pattern allows for creation of object inside another class helping with decoupling and maintainability.
In the above example it is used to instantiate a model with a different set of commands and a data table.
It's advantages are that it is reusable for different kinds of models just by creating a new class for it. This way the 
implementation is not tied to any model which therefore allows for an easier time adding features in the future.

2.

```java
import java.util.LinkedList;

public abstract class CommandHandler {
    private final LinkedList<CommandHandler> handlers = new LinkedList<>();

    public CommandHandler setNext(CommandHandler next) {
        handlers.addLast(next);
        return next;
    }

    public void handle(Config config) {
        if (canHandle(config)) {
            execute(config);
        }
    }

    public abstract boolean canHandle(Config config) {
        return config.get("broadcast");
    }

    public abstract void execute(Config config) {
        //executing the command :)
    }
}
```

___
