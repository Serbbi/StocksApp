package nl.rug.aoop.networking.client;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.client.messagehandlers.MessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Client can connect to a server and send message to it.
 */
@Slf4j
public class Client implements Runnable {
    private final int TIMEOUT = 69000;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    @Getter
    private boolean running = false;
    @Getter
    private boolean connected = false;
    private final MessageHandler messageHandler;

    /**
     * Client constructor.
     *
     * @param address        to connect to the server.
     * @param messageHandler to manipulate the messages.
     * @throws IOException exception to be thrown in case something goes wrong.
     */
    public Client(InetSocketAddress address, MessageHandler messageHandler) throws IOException {
        initSocket(address);
        this.messageHandler = messageHandler;
        connected = true;
    }

    /**
     * Initialize the socket to establish a connection with the server.
     *
     * @param address to connect to the server.
     * @throws IOException exception to be thrown in case something goes wrong.
     */
    public void initSocket(InetSocketAddress address) throws IOException {
        socket = new Socket();
        socket.connect(address, TIMEOUT);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        if (!socket.isConnected()) {
            throw new IOException("Socket is not connected!");
        } else {
            log.info("Connected!");
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message to be sent(string).
     */
    public void sendMessage(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Invalid message!");
        }
        out.println(message);
    }

    /**
     * Implements the run method from the Runnable interface.
     */
    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                String serverResponse = in.readLine();
                log.info("Server response: " + serverResponse);
                if (serverResponse == null || serverResponse.trim().equalsIgnoreCase("cya")) {
                    log.info("Client Exited!");
                    terminate();
                    break;
                }
                messageHandler.handleMessage(serverResponse);
            } catch (SocketException e) {
                // If server is closed unexpectedly, thread is terminated
                if (e.getMessage().equals("Connection reset")) {
                    log.info("Server Offline!");
                } else {
                    log.error(e.getMessage());
                }
                try {
                    terminate();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                break;
            } catch (IOException e) {
                log.error("Error occurred while trying to communicate with the server!", e);
            }
        }
    }

    /**
     * Method to end the loop when the client disconnects.
     */
    public void terminate() throws IOException {
        log.warn("Terminating client!");
        socket.close();
        running = false;
        connected = false;
    }

}
