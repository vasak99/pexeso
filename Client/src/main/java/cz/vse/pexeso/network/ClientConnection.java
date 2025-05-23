package cz.vse.pexeso.network;

import cz.vse.pexeso.common.utils.StreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles the connection to the server. Sends and receives messages from the server.
 */
public class ClientConnection {
    private static final Logger log = LoggerFactory.getLogger(ClientConnection.class);
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private volatile boolean isListening = true;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private MessageHandler messageHandler;

    public ClientConnection(String host, int port) {
        try {
            log.info("Creating client connection to {}:{}", host, port);
            this.socket = new Socket(host, port);
            this.oos = new ObjectOutputStream(this.socket.getOutputStream());
            this.oos.flush();
            this.ois = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            log.error("Error creating client connection: ", e);
            close();
        }
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
        listenForMessage();
    }

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

    private void listenForMessage() {
        log.info("Starting new thread to listen for messages");
        Thread listenerThread = new Thread(() -> {
            // Loop while the connection is considered active
            while (isListening && socket != null && !socket.isClosed()) {
                try {
                    String messageFromServer = StreamReader.readPacket(ois);
                    log.debug("Message from server: {}", messageFromServer);
                    if (messageHandler != null) {
                        messageHandler.dispatch(messageFromServer);
                    }
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
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void close() {
        if (!isListening) {
            return;
        }

        log.info("Closing client connection");
        isListening = false;
        closeResources();
    }

    private void closeResources() {
        //check if already closed
        if (isClosed.getAndSet(true)) {
            return;
        }

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
