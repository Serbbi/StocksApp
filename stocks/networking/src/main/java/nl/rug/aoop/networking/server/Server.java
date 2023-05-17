package nl.rug.aoop.networking.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.client.messagehandlers.MessageHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server to which clients connect and send messages.
 */
@Slf4j
public class Server implements Runnable {

    private final ServerSocket serverSocket;

    @Getter
    private final int port;
    @Getter
    private boolean running = false;
    private final ExecutorService executorService;
    private final MessageHandler messageHandler;
    @Getter
    private final Map<UUID, ClientHandler> clientHandlers;

    private final ClientLoginHandler clientLoginHandler;

    /**
     * Server constructor.
     *
     * @param messageHandler     Message handler instance which will handle all received messages.
     * @param port               port to host the server on.
     * @param clientLoginHandler Handles the login for each client handler. Passed to each login
     *                           handler when it is created.
     * @throws IOException exception to be thrown when something goes wrong.
     */
    public Server(MessageHandler messageHandler, int port, ClientLoginHandler clientLoginHandler) throws IOException {
        this.messageHandler = messageHandler;
        this.executorService = Executors.newCachedThreadPool();
        this.serverSocket = new ServerSocket(port);
        this.port = serverSocket.getLocalPort();
        this.clientHandlers = new HashMap<>();
        this.clientLoginHandler = clientLoginHandler;
    }

    /**
     * Running loop for the server.
     */
    public void run() {
        running = true;
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, messageHandler, clientLoginHandler);
                log.info("New client connected: " + clientHandler.getId());
                executorService.submit(clientHandler);
                clientHandlers.put(clientHandler.getId(), clientHandler);
            } catch (IOException e) {
                log.error("Error occurred while trying to accept a client connection!", e);
            }
        }
    }

    /**
     * Sends a response to a client.
     *
     * @param clientHandlerID The uuid of the client handler for the client that needs a response.
     * @param response        The response message to send.
     */
    public void sendResponse(UUID clientHandlerID, String response) {
        clientHandlers.get(clientHandlerID).sendResponse(response);
    }

    public int getNumConnectedClients() {
        return clientHandlers.size();
    }

    /**
     * Stops the server.
     *
     * @throws IOException Thrown when server cannot be stopped.
     */
    public void terminate() throws IOException {
        running = false;
        executorService.shutdownNow();
        serverSocket.close();
        clientHandlers.keySet().forEach(key -> clientHandlers.get(key).terminate());
    }
}
