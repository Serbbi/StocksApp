# Question 1

Suppose you are developing a similar (if not identical) project for a company. One teammate poses the following:

> "We do not have to worry about logging. The application is very small and tests should take care of any potential
> bugs. If we really need it, we can print some important data and just comment it out later."

Do you agree or disagree with the proposition? Please elaborate on your reason to agree or disagree. (~50-100 words)

___

**Answer**:

No. Logging can be useful in any application regardless of the size. Firstly it is useful during development to be able
to see what the program is doing, without having to worry about removing print statements. Often the information that
the developer is outputting might be useful in the future for debugging, and the developer might have to go and add the
print statements in again. Overall it just makes development a lot more difficult as the developer has no way of tracing
back an issue without reproducing the error.
___

# Question 2

One of your requirements is to create a message class where `key` and `value` are strings. How could you modify your
class so that the key and value could be any different data types and do not require casting by the developer?
Preferably, provide the code of the modified class in the answer.
___

**Answer**:

To achieve this, we could use generics to allow instances of any type to be used as key or value. This would allow a
developer to pass any type for key and value, and it would not require any casting of types.

```java
/**
 * Message class that uses generics to store header and value as any type.
 * @param <T> Header type.
 * @param <E> Message type.
 */
class Message<T, E> {
    private final T header;
    private final E value;
    private final LocalDateTime timestamp;

    public Message(T header, E value) {
        this.header = header;
        this.value = value;
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public E getValue() {
        return value;
    }

    public T getHeader() {
        return header;
    }
}
```

___

# Question 3

How is Continuous Integration applied to (or enforced on) your assignment? (~30-100 words)

___

**Answer**:

In this assignment, automated tests are used in GitHub to ensure code quality and ensure that all the tests have passed.
Maven clean compile is used to check that out code is well styled, and adequately commented. The unit tests we wrote are
also run to ensure that they all pass before a pull request passes. This use of continuous integration ensures that out
code meets a set standard before a pull request will pass to accept our submission.

___

# Question 4

One of your colleagues wrote the following class:

```java
import java.util.*;

public class MyMenu {

    private final Map<Integer, PlayerAction> actions;

    public MyMenu() {
        actions = new HashMap<>();
        actions.put(0, DoNothingAction());
        actions.put(1, LookAroundAction());
        actions.put(2, FightAction());
    }

    public void printMenuOptions(boolean isInCombat) {
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add("What do you want to?");
        menuOptions.add("\t0) Do nothing");
        menuOptions.add("\t1) Look around");
        if (isInCombat) {
            menuOptions.add("\t2) Fight!");
        }
    }

    public void doOption() {
        int option = getNumber();
        if (actions.containsKey(option)) {
            actions.get(option).execute();
        }
    }

    public int getNumber() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }
}
```

List at least 2 things that you would improve, how it relates to test-driven development and why you would improve these
things. Provide the improved code below.

___

**Answer**:

- warning the user if the key they entered is not existent 
- re-prompt in case that the user enters anything instead of a number

This relates to test-driven development by the fact that you can more easily find bugs in the code in the perspective of
the user. The improvement of these will provide a better user experience and exterminate the bugs for an easier time 
testing the code in the future. 

Improved code:

```java
import java.util.*;

public class MyMenu {

    private final Map<Integer, PlayerAction> actions;

    public MyMenu() {
        actions = new HashMap<>();
        actions.put(0, DoNothingAction());
        actions.put(1, LookAroundAction());
        actions.put(2, FightAction());
    }

    public void printMenuOptions(boolean isInCombat) {
        List<String> menuOptions = new ArrayList<>();
        menuOptions.add("What do you want to?");
        menuOptions.add("\t0) Do nothing");
        menuOptions.add("\t1) Look around");
        if (isInCombat) {
            menuOptions.add("\t2) Fight!");
        }
    }

    public void doOption() {
        while (true) {
            int option = getNumber();
            if (actions.containsKey(option)) {
                actions.get(option).execute();
                break;
            } else {
                System.out.println("Option not available!");
            }
        }
    }

    public int getNumber() {
        Scanner scanner = new Scanner(System.in);
        int input = null;
        while (input == null) {
            try {
                input = scanner.nextInt();
            } catch (Exception InputMismatchException) {
                System.out.println("Wrong Input!");
                input = null;
            }
        }
        return input;
    }
}
```

___
