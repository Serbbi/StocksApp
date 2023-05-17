package nl.rug.aoop.networking.server;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.client.messagehandlers.MessageHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@Slf4j
class ClientHandlerTest {
    private int serverPort;
    private boolean serverStarted;
    private Socket serverSideSocket;

    /**
     * Starts a basic server in order to test the client handler.
     */
    @BeforeEach
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

    @AfterEach
    void stopServer() throws IOException {
        serverStarted = false;
        serverSideSocket.close();
    }

    private ClientLoginHandler getMockClientLoginHandler() {
        return Mockito.mock(ClientLoginHandler.class);
    }

    /**
     * Tests that if a message is received by the ClientHandler, it is forwarded to the MessageHandler.
     *
     * @throws IOException Thrown on socket.
     */
    @Test
    void testServerReceiveMessage() throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", serverPort));
            await().atMost(1, TimeUnit.SECONDS).until(() -> serverSideSocket != null);

            MessageHandler messageHandler = Mockito.mock(MessageHandler.class);
            ClientHandler clientHandler = new ClientHandler(serverSideSocket, messageHandler, getMockClientLoginHandler());

            new Thread(clientHandler).start();
            await().atMost(1, TimeUnit.SECONDS).until(clientHandler::isRunning);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String message = "Test Message";
            out.println(message);

            Mockito.verify(messageHandler, timeout(5000).times(1)).handleMessage(message);
        }

    }

    /**
     * Verifies that when a login message is received from the client, then the client handler
     * runs the login method of the {@link ClientLoginHandler}.
     *
     * @throws IOException Thrown on socket.
     */
    @Test
    void testClientLogin() throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", serverPort));
            await().atMost(1, TimeUnit.SECONDS).until(() -> serverSideSocket != null);

            MessageHandler messageHandler = Mockito.mock(MessageHandler.class);
            ClientLoginHandler loginHandler = Mockito.mock(ClientLoginHandler.class);
            ClientHandler clientHandler = new ClientHandler(serverSideSocket, messageHandler, loginHandler);

            new Thread(clientHandler).start();
            await().atMost(1, TimeUnit.SECONDS).until(clientHandler::isRunning);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String message = "login: testID";
            out.println(message);

            ArgumentCaptor<UUID> uuid = ArgumentCaptor.forClass(UUID.class);
            ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

            verify(loginHandler).login(uuid.capture(), id.capture());
            assertEquals(clientHandler.getId(), uuid.getValue());
            assertEquals("testID", id.getValue());
        }
    }

}