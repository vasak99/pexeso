package cz.vse.pexeso.network;

import cz.vse.pexeso.helper.AppServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * Handles the connection to the server. Sends and receives messages from the server.
 */
public class ClientConnection {
    public static final Logger log = LoggerFactory.getLogger(ClientConnection.class);
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private volatile boolean isListening = true;

    /**
     * Initializes the socket and the input/output streams.
     * Starts a new thread to listen for messages from the server.
     */
    public ClientConnection() {
        try {
            log.info("Creating client connection");
            this.socket = new Socket("localhost", 8080);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            listenForMessage();
        } catch (IOException e) {
            log.error("Error creating client connection: ", e);
            closeResources(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to be sent.
     */
    public void sendMessage(String message) {
        try {
            log.debug("Sending message: {}", message);
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            log.error("Error sending message: ", e);
            closeResources(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Listens for messages from the server in a separate thread.
     * Sends the message to MessageHandler to parse and handle it.
     */
    public void listenForMessage() {
        log.info("Starting new thread to listen for messages");
        new Thread(() -> {
            String messageFromServer;
            // Loop while the connection is considered active
            while (isListening && socket != null && !socket.isClosed()) {
                try {
                    messageFromServer = bufferedReader.readLine();
                    // If the server has closed the connection, readLine() returns null
                    if (messageFromServer == null) {
                        log.warn("Server closed the connection");
                        break;
                    }
                    log.debug("Message from server: {}", messageFromServer);
                    AppServices.getMessageHandler().parseMessage(messageFromServer);
                } catch (IOException e) {
                    log.error("Error reading message from server in listener thread: ", e);
                    break;
                }
            }
            // Loop is exited, listening is stopped
            isListening = false;
            closeResources(socket, bufferedReader, bufferedWriter);
        }).start();
    }

    /**
     * Closes the resources used for the connection.
     *
     * @param socket         The socket to be closed.
     * @param bufferedReader The BufferedReader to be closed.
     * @param bufferedWriter The BufferedWriter to be closed.
     */
    public void closeResources(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        log.info("Closing resources");
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            log.error("Error closing resources: ", e);
        }
    }
}

