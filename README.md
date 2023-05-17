<br />
<p align="center">
  <h1 align="center">Stock Market Simulation</h1>

  <p align="center">
    A small scale stock market simulation that communicates over a network, and uses bots to simulate clients interacting with the market.
  </p>

## Table of Contents

* [About the Project](#about-the-project)
    * [Built With](#built-with)
* [Getting Started](#getting-started)
    * [Running](#running)
* [Modules](#modules)
* [Notes](#notes)
* [Evaluation](#evaluation)
* [Extras](#extras)

## About The Project

This is a project which consists of a simulation of a simplified stock market. It consists of 2 parts that are the
Stock Exchange and the Trader Application. These 2 applications communicate over a network, the server being the Stock
Application which can handle multiple connections from the Trader Application. Traders can trade shares by sending buy
or sell orders to the exchange. The exchange keeps track of all the buy and sell orders (the bids and asks). The orders
contain information about whether they are of type buy or sell, the stock, number of shares and the price. An example
of the whole process goes like this: An order is created by one of the traders, it is passed through the network to the
server side where is enqueued; After this, the order is being polled from the queue and processed; The processing
consists of checking the other types of orders available and matching them; Resolving the orders results in the buyer
seller receiving transactions and the information about the respective traders is updated and also the information about
all the stocks.

## Getting Started

[//]: # (To get a local copy up and running follow these simple steps.)

[//]: # ()

[//]: # (### Prerequisites)

[//]: # ()

[//]: # (* [Java 17]&#40;https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html&#41; or higher)

[//]: # (* [Maven 3.6]&#40;https://maven.apache.org/download.cgi&#41; or higher)

[//]: # ()

[//]: # (### Installation)

[//]: # ()

[//]: # (1. Navigate to the `stocks` directory)

[//]: # (2. Clean and build the project using:)

[//]: # ()

[//]: # (```sh)

[//]: # (mvn install)

[//]: # (```)

[//]: # ()

### Running

<!--
Describe how to run your program here. These should be a few very simple steps. 
-->

This application is run in Docker using Docker Compose. This makes getting the application up and running extremely easy

From the root of the project, run:

```bash
docker-compose up -d --build
```

The web interface can then be found at [localhost:3000](http://localhost:3000).

## Modules

<!--
Describe each module in the project, what their purpose is and how they are used in your program. Try to aim for at least 100 words per module.
-->

### Command

This module contains the infrastructure for implementing a Command Pattern. This is used throughout the other modules.

The command interface defines the behaviour that a command must perform. Mainly, it defines the execute method which
each command must have.

The `CommandHandler` executes the `Command` that is stored in the hash map that it holds. It uses a key to retrieve the
command and then execute it.

### Message Queue

This module has all the message related classes. The `Message` class is how we are sending information from clients to
the server. There are multiple `MessageQueue`'s available, the main one is the `ThreadSafeQueue` which is used in the
stocks app. The `NetworkProducer` is the class which sends messages over the network to be enqueue, and the stocks app
implements the `MQConsumer` interface to continuously poll messages. The `MQMessageHandler` is an implementation of
`MessageHandler` to pass the message to a command handler. In our case we have the `MQPutCommand` which enqueues a
message on a queue.

### Networking

This module has all the network classes. This includes the generic server with client managers, and clients. This is
the groundwork for the other modules to communicate over the network. The server can handle multiple simultaneously
connected clients using the `ClientHandler`. The `ClientHandler` interacts directly with the client over the network.
All requests that are received from the client are forwarded onto the `MessageHandler`. This is what allows other
classes to communicate over the network using this module.

The `Client`, much like the `ClientHandler` has a `MessageHandler` which is how data is sent to other classes to be
used.

The `MessageHandler` is what allows the client and server to be used by other modules. To define a behaviour that uses
the client of server, we just have to create an implementation of `MessageHandler`. This is crucial to allowing the
networking to be generic.

### Stock Market UI

Provides a user interface for the stocks application. This is what displays all the stock market information.

### Stocks App

A network server application that clients can connect to purchase and sell stocks. This app can accept multiple
connected clients simultaneously using the server and client handers defined in the Networking module. Requests are sent
to the server as a `NetworkMessage`. The `header` of the `NetworkMessage` is passed into a command handler and
the `value` is processed by the corresponding command. At the moment, all requests that the server receives are '
MqPut' commands, which adds the messages to the message queue, so that they can then be processed one by one. This
message queue is thread-safe to ensure concurrency, as it is accessed and mutated
from multiple threads.

The stocks app polls the message queue continuously and processes the message that is enqueued.

### Traders App

This module creates bots that connect to the stocks application, bots which create buy and sell limit orders and send them
to the server.
Each orderBot behaves has its own network client, and runs on its own thread. The bots generate random buy and sell
orders. This is done with the order generators. We have designed this in such a way that we can design multiple types of
generators that to generate orders in different ways. Currently, we only have the `RandomOrderGenerator`.

This module makes use of the Factory design pattern to generate create the bots. ([Builder pattern](#Builder Pattern))

### Util

This module provides all the classes that are used in both the [Stocks App](#Stock App) and
the [Traders App](#Traders App) to prevent dependency between the two modules. These classes are primarily data classes.
This module also contains all the file loaders that load the traders and stocks data from the yaml files.

## Design

<!--
List all the design patterns you used in your program. For every pattern, describe the following:
- Where it is used in your application.
- What benefit it provides in your application. Try to be specific here. For example, don't just mention a pattern improves maintainability, but explain in what way it does so.
-->

### Command Pattern

We used the command pattern multiple times throughout the program. In both the [Stocks App](#Stock App) and
the [Traders App](#Traders App) when a `NetworkMessage` is received, the headers is passed into the command handlers to
execute the command specified by the network message.

### Builder Pattern

We used this design pattern to simplify the creation of our command handlers. Throughout the project, we have
implemented `CommandHandlerBuilder` twice. Once in the [Stocks App](#Stock App) to create the main stocks
command handler, and also in the [Traders App](#Traders App) to create the `StocksClientHandler` command handlers.

This pattern simplifies the process of creating complex objects and separates the complex object from it's construction.
In this case, we have encapsulated the `CommandHandler` creation logic within a `CommandHandlerBuilder`, and can add and
remove different sets of commands by running or not running certain factory methods.

### Factory Method Pattern

This pattern was used to create the bots in the [Traders App](#Traders App). We generalised this to allow future
development of other types of bots. At the moment we only have the `OrderBot` class, but the `BotFactory` class could be
used to create more types of bots.

The factory pattern encapsulates the creation logic for an object, and in this case it is being used to encapsulate the
creation logic for a `Bot`.

## Evaluation

Having run the simulation a few times and for longer periods of time, everything seems to be working fine. One thing
that is worth noting is that our processing of orders is done in O(1) time because of our use of maps. We have done
plenty of unit tests for the project, although in the latter stages of development we encountered some issues with our
code which needed many runs and logging lots of methods to trace the problems. In the end we think we got rid of most
bugs(hopefully). A feature that it's still in the plan is the one where we keep track of the shares a seller has put on
offer, so that when a buyer wants some shares and the seller has no more shares of that kind, but
the order is still available, we don't have to send a response that the order is no longer accessible.
<!--
Discuss the stability of your implementation. What works well? Are there any bugs? Is everything tested properly? Are there still features that have not been implemented? Also, if you had the time, what improvements would you make to your implementation? Are there things which you would have done completely differently? Try to aim for at least 250 words.
-->

## Teamwork
This project was developed by [Andrew Rutherfoord](https://github.com/AndrewRutherfoord) and [Serban Tonie](https://github.com/Serbbi)
as an assignment at the `Advanced Object Oriented Programming` course at University of Groningen.

___


<!-- Below you can find some sections that you would normally put in a README, but we decided to leave out (either because it is not very relevant, or because it is covered by one of the added sections) -->

<!-- ## Usage -->
<!-- Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources. -->

<!-- ## Roadmap -->
<!-- Use this space to show your plans for future additions -->

<!-- ## Contributing -->
<!-- You can use this section to indicate how people can contribute to the project -->

<!-- ## License -->
<!-- You can add here whether the project is distributed under any license -->


<!-- ## Contact -->
<!-- If you want to provide some contact details, this is the place to do it -->

<!-- ## Acknowledgements  -->
