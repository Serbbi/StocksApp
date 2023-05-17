package nl.rug.aoop.networking.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.rug.aoop.networking.client.messagehandlers.MessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;

/**
 * ClientHandler connects clients to the server, sending messages back and forth
 * from the client to the server.
 */
@Slf4j
public class ClientHandler implements Runnable {
    @Getter
    private final UUID id;
    private static final String EXIT_MESSAGE = "cya";
    private final Socket socket;
    private final PrintWriter out;
    private final BufferedReader in;
    @Getter
    private boolean running = false;

    private final MessageHandler messageHandler;
    private final ClientLoginHandler loginHandler;

    /**
     * ClientHandler constructor.
     *
     * @param messageHandler the message handler instance that should handle the received messages.
     * @param socket         where the client and server communicate through.
     * @param loginHandler   the login handler instance which is used to identify a clienthandler for the app.
     * @throws IOException exception to be thrown when something goes wrong.
     */
    public ClientHandler(Socket socket, MessageHandler messageHandler,
                         ClientLoginHandler loginHandler) throws IOException {
        this.id = UUID.randomUUID();
        this.socket = socket;
        this.messageHandler = messageHandler;
        this.loginHandler = loginHandler;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Sends a response back to the client.
     *
     * @param response The string which must be sent to the client.
     */
    public void sendResponse(String response) {
        out.println(response);
    }

    /**
     * Implements the run method from the Runnable interface.
     */
    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                String input = in.readLine();

                if (input != null) {
                    log.info("Handling input: \"" + input + "\"");
                    if (input.startsWith("login: ")) {
                        loginHandler.login(id, input.replace("login: ", ""));
                    } else if (input.equals(EXIT_MESSAGE)) {
                        terminate();
                    } else {
                        messageHandler.handleMessage(input);
                    }
                }
            } catch (SocketException e) {
                // If client is closed unexpectedly, thread is terminated
                if (e.getMessage().equals("Connection reset")) {
                    log.info("Client Disconnected!");
                } else {
                    log.error(e.getMessage());
                }
                terminate();
            } catch (IOException e) {
                log.error("Error occurred while trying to accept a client connection!", e);
            }
        }
    }

    /**
     * Terminate the connection between client and server.
     */
    public void terminate() {
        log.info("Terminating server.");
        running = false;
        try {
            // Kills the client
            out.println(EXIT_MESSAGE);
            socket.close();
        } catch (IOException e) {
            log.error("Cannot close the socket!", e);
        }
    }

    public static String getExitMessage() {
        return EXIT_MESSAGE;
    }
}
