package cz.vse.pexeso.network;

import cz.vse.pexeso.common.utils.StreamReader;
import cz.vse.pexeso.service.AppServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles the connection to the server. Sends and receives messages from the server.
 */
public class ClientConnection {
    public static final Logger log = LoggerFactory.getLogger(ClientConnection.class);
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private volatile boolean isListening = true;

    /**
     * Initializes the socket and the input/output streams.
     * Starts a new thread to listen for messages from the server.
     */
    public ClientConnection() {
        try {
            log.info("Creating client connection");
            this.socket = new Socket("localhost", 8080);
            this.oos = new ObjectOutputStream(this.socket.getOutputStream());
            this.oos.flush();
            this.ois = new ObjectInputStream(this.socket.getInputStream());
            listenForMessage();
        } catch (IOException e) {
            log.error("Error creating client connection: ", e);
            close();
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
            StreamReader.writePacket(oos, message);
            oos.flush();
        } catch (IOException e) {
            log.error("Error sending message: ", e);
            close();
        }
    }

    /**
     * Listens for messages from the server in a separate thread.
     * Sends the message to MessageHandler to parse and handle it.
     */
    public void listenForMessage() {
        log.info("Starting new thread to listen for messages");
        new Thread(() -> {
            // Loop while the connection is considered active
            while (isListening && socket != null && !socket.isClosed()) {
                String messageFromServer;
                try {
                    messageFromServer = StreamReader.readPacket(ois);
                    log.debug("Message from server: {}", messageFromServer);
                    AppServices.getMessageHandler().dispatch(messageFromServer);
                } catch (EOFException eof) {
                    log.warn("Server closed the connection: ", eof);
                    break;
                } catch (Exception e) {
                    log.error("Error reading message from server in listener thread: ", e);
                    break;
                }
            }
            // Loop is exited, listening is stopped
            isListening = false;
            close();
        }).start();
    }

    public void close() {
        log.info("Closing client connection");
        isListening = false;
        closeResources();
    }

    /**
     * Closes the resources used for the connection.
     */
    private void closeResources() {
        log.info("Closing resources");
        try {
            if (ois != null) {
                ois.close();
            }
            if (oos != null) {
                oos.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            log.error("Error closing resources: ", e);
        }
    }
}

