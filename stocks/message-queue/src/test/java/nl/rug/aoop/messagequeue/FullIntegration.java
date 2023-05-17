package nl.rug.aoop.messagequeue;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.command.CommandHandler;
import nl.rug.aoop.messagequeue.commands.MqPutCommand;
import nl.rug.aoop.messagequeue.message.Message;
import nl.rug.aoop.messagequeue.message.ThreadSafeOrderedMessageQueue;
import nl.rug.aoop.messagequeue.messageHandlers.MQMessageHandler;
import nl.rug.aoop.messagequeue.producer.NetworkProducer;
import nl.rug.aoop.networking.client.Client;
import nl.rug.aoop.networking.client.messagehandlers.MessageHandler;
import nl.rug.aoop.networking.client.messagehandlers.MessageLogger;
import nl.rug.aoop.networking.server.ClientHandler;
import nl.rug.aoop.networking.server.ClientLoginHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
public class FullIntegration {
    private int serverPort;
    private boolean serverStarted;
    private Socket serverSideSocket;

    void startServer() {
        new Thread(() -> {
            try {
                ServerSocket s = new ServerSocket(0);
                serverPort = s.getLocalPort();
                serverStarted = true;

                serverSideSocket = s.accept();
                log.info("Test Server Started...");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        await().atMost(2, TimeUnit.SECONDS).until(() -> serverStarted);
    }

    /**
     * Test that a mqput command message can be sent from a client to the server,
     * and result in the message being added to a message queue.
     */
    @Test
    void clientAddMessageToServerQueue() throws IOException {
        startServer();

        MessageHandler messageHandler = new MessageLogger();
        Client client = new Client(new InetSocketAddress("localhost", serverPort), messageHandler);
        NetworkProducer networkProducer = new NetworkProducer(client);
        CommandHandler commandHandler = new CommandHandler();
        ThreadSafeOrderedMessageQueue queue = new ThreadSafeOrderedMessageQueue();
        MqPutCommand mqPutCommand = new MqPutCommand(queue);
        commandHandler.addCommand("MqPut", mqPutCommand);
        MQMessageHandler mqMessageHandler = new MQMessageHandler(commandHandler);
        ClientHandler clientHandler = new ClientHandler(serverSideSocket, mqMessageHandler, Mockito.mock(ClientLoginHandler.class));
        new Thread(clientHandler).start();

        Message message = new Message("TESTHEADER", "TESTBODY");
        networkProducer.put(message);

        await().atMost(2, TimeUnit.SECONDS).until(() -> queue.getSize() == 1);
        Message dequeued = queue.dequeue();
        assertEquals(dequeued.getValue(), message.getValue());
        assertEquals(dequeued.getHeader(), message.getHeader());
        assertEquals(dequeued.getTimestamp(), message.getTimestamp());
    }

    /**
     * Test that a close client command message send by the client results in both the ClientHandler
     * and the Client are terminated.
     */
    @Test
    void clientExitTerminateClientHandlerAndClient() throws IOException {
        startServer();

        MessageHandler messageHandler = new MessageLogger();
        Client client = new Client(new InetSocketAddress("localhost", serverPort), messageHandler);
        CommandHandler commandHandler = new CommandHandler();
        ThreadSafeOrderedMessageQueue queue = new ThreadSafeOrderedMessageQueue();
        MqPutCommand mqPutCommand = new MqPutCommand(queue);
        commandHandler.addCommand("MqPut", mqPutCommand);
        MQMessageHandler mqMessageHandler = new MQMessageHandler(commandHandler);
        ClientHandler clientHandler = new ClientHandler(serverSideSocket, mqMessageHandler, Mockito.mock(ClientLoginHandler.class));
        new Thread(clientHandler).start();

        client.sendMessage("cya");

        await().atMost(1, TimeUnit.SECONDS).until(() -> !clientHandler.isRunning());
        await().atMost(1, TimeUnit.SECONDS).until(() -> !client.isRunning());
        assertFalse(clientHandler.isRunning());
        assertFalse(client.isRunning());
    }
}
