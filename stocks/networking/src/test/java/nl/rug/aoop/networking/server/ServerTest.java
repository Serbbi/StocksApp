package nl.rug.aoop.networking.server;

import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.client.messagehandlers.MessageHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@Slf4j
class ServerTest {
    private Server server;
    private ClientLoginHandler clientLoginHandler;
    private ExecutorService executorService;

    void createClient(InetSocketAddress address) {
        AtomicBoolean clientConnected = new AtomicBoolean(false);
        Thread t = new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(address, 1000);

                if (!socket.isConnected()) {
                    throw new IOException("Socket is not connected!");
                } else {
                    clientConnected.set(true);
                    log.info("Test Client Connected!");
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
//        t.start();
        executorService.submit(t);
    }

    ClientLoginHandler getMockClientLoginHandler() {
        clientLoginHandler = Mockito.mock(ClientLoginHandler.class);
        return clientLoginHandler;
    }

    @BeforeEach
    void setUp() throws IOException {
        this.executorService = Executors.newCachedThreadPool();

        MessageHandler messageHandler = Mockito.mock(MessageHandler.class);

        server = new Server(messageHandler, 0, getMockClientLoginHandler());

        new Thread(server).start();
    }

    @AfterEach
    void tearDown() throws IOException {
        executorService.shutdown();
        server.terminate();
    }

    @Test
    void testRun() throws IOException {
        await().atMost(1, TimeUnit.SECONDS).until(server::isRunning);
        assertTrue(server.isRunning());
    }

//    @Test
    void testSingleConnection() throws IOException {
        testRun();

        InetSocketAddress address = new InetSocketAddress("localhost", server.getPort());

        assertEquals(1, server.getNumConnectedClients());
    }

//    @Test
    void testConnectMultipleClients() throws IOException {
        testRun();

        InetSocketAddress address = new InetSocketAddress("localhost", server.getPort());

        createClient(address);
        createClient(address);
        createClient(address);

        assertEquals(3, server.getNumConnectedClients());
    }

    @Test
    void testSendReponse() throws IOException {

        InetSocketAddress address = new InetSocketAddress("localhost", server.getPort());
        AtomicBoolean clientConnected = new AtomicBoolean(false);

        new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(address, 1000);

                if (!socket.isConnected()) {
                    throw new IOException("Socket is not connected!");
                } else {
                    clientConnected.set(true);

                    log.info("Test Client Connected!");

                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.println("login: testID" );
                    out.flush();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        ArgumentCaptor<UUID> uuid = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> id = ArgumentCaptor.forClass(String.class);

        verify(clientLoginHandler, timeout(5000)).login(uuid.capture(), id.capture());

        log.info("ClientHandler UUID:"+ uuid.getValue());
        server.sendResponse(uuid.getValue(), "hello world!");
    }
}