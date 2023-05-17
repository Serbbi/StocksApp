package nl.rug.aoop.networking.client;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.client.messagehandlers.MessageHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class ClientTest {
    private int serverPort;
    private boolean serverStarted = false;

    private PrintWriter serverSideOut;
    private BufferedReader serverSideIn;

    @BeforeEach
    void startServer() {
        new Thread(() -> {
            try {

                ServerSocket serverSocket = new ServerSocket(0);
                serverPort = serverSocket.getLocalPort();
                serverStarted = true;

                Socket serverSideSocket = serverSocket.accept();
                serverSideOut = new PrintWriter(serverSideSocket.getOutputStream(), true);
                serverSideIn = new BufferedReader(new InputStreamReader(serverSideSocket.getInputStream()));
                log.info("Test Server Started...");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        await().atMost(2, TimeUnit.SECONDS).until(() -> serverStarted);
    }

    @Test
    public void testClientRun() throws IOException {
        InetSocketAddress address = new InetSocketAddress("localhost", serverPort);
        MessageHandler mockHandler = Mockito.mock(MessageHandler.class);
        Client client = new Client(address, mockHandler);

        new Thread(client).start();
        await().atMost(1, TimeUnit.SECONDS).until(client::isRunning);
        assertTrue(client.isRunning());
        assertTrue(client.isConnected());
    }

    @Test
    public void testReadSingleMessage() throws IOException {
        InetSocketAddress address = new InetSocketAddress("localhost", serverPort);
        MessageHandler mockHandler = Mockito.mock(MessageHandler.class);
        Client client = new Client(address, mockHandler);

        new Thread(client).start();
        await().atMost(1, TimeUnit.SECONDS).until(client::isRunning);
        assertTrue(client.isRunning());

        String message = "Test Message bla bla";
        serverSideOut.println(message);

        await().untilAsserted(() -> Mockito.verify(mockHandler).handleMessage(message));
    }

    @Test
    public void testSendNullMessage() throws IOException {
        InetSocketAddress address = new InetSocketAddress("localhost", serverPort);
        MessageHandler mockHandler = Mockito.mock(MessageHandler.class);
        Client client = new Client(address, mockHandler);

        new Thread(client).start();
        await().atMost(1, TimeUnit.SECONDS).until(client::isRunning);
        assertTrue(client.isRunning());

        assertThrows(IllegalArgumentException.class, () -> client.sendMessage(null));
    }

    @Test
    public void testSendMessage() throws IOException {
        InetSocketAddress address = new InetSocketAddress("localhost", serverPort);
        MessageHandler mockHandler = Mockito.mock(MessageHandler.class);
        Client client = new Client(address, mockHandler);

        new Thread(client).start();
        await().atMost(1, TimeUnit.SECONDS).until(client::isRunning);
        assertTrue(client.isRunning());

        String message = "Test Message!";
        client.sendMessage(message);

        assertEquals(message, serverSideIn.readLine());

    }

    @Test
    public void testClientTerminateConnection() throws IOException {
        InetSocketAddress address = new InetSocketAddress("localhost", serverPort);
        MessageHandler mockHandler = Mockito.mock(MessageHandler.class);
        Client client = new Client(address, mockHandler);

        new Thread(client).start();
        await().atMost(1, TimeUnit.SECONDS).until(client::isRunning);
        assertTrue(client.isRunning());

        String message = "cya";
        serverSideOut.println(message);

        await().atMost(2, TimeUnit.SECONDS).until(() -> !client.isRunning());
        assertFalse(client.isRunning());
        assertFalse(client.isConnected());
    }

}
