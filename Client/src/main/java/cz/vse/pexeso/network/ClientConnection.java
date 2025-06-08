package cz.vse.pexeso.network;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.utils.StreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages socket connection to the server, sends and receives messages.
 * Uses a background thread for listening, and dispatches messages through MessageHandler.
 * Closes resources on error or shutdown or when instructed to.
 *
 * @author kott10
 * @version June 2025
 */
public class ClientConnection {
    private static final Logger log = LoggerFactory.getLogger(ClientConnection.class);
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final AtomicBoolean listenerStarted = new AtomicBoolean(false);
    private final AtomicBoolean isActive = new AtomicBoolean(true);
    private MessageHandler messageHandler;

    /**
     * Constructs a new ClientConnection by connecting to the given host and port.
     * Creates input/output streams.
     *
     * @param host the server hostname or IP
     * @param port the server port number
     */
    public ClientConnection(String host, int port) {
        try {
            log.info("Creating client connection to {}:{}", host, port);
            this.socket = new Socket(host, port);
            this.oos = new ObjectOutputStream(this.socket.getOutputStream());
            this.oos.flush();
            this.ois = new ObjectInputStream(this.socket.getInputStream());
            log.info("ClientConnection established");
        } catch (IOException e) {
            log.error("Error creating client connection: ", e);
            close();
        }
    }

    /**
     * Registers a MessageHandler to be called whenever a new message arrives.
     * If the listener thread has not started yet and the connection is active, it starts the listener thread.
     *
     * @param handler the MessageHandler to notify
     */
    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
        if (isActive.get() && listenerStarted.compareAndSet(false, true)) {
            listenForMessage();
        }
    }

    /**
     * Sends a string message over the socket using a via StreamReader. Closes the connection on IOException.
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        if (!isActive.get()) {
            log.warn("Attempted to send message but connection is inactive");
            return;
        }

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
     * Starts a thread that continuously reads string packets from the server using StreamReader.
     * Each received string is converted to Message object and sent to MessageHandler.
     * The listener stops on socket closure or errors and cleans up.
     */
    private void listenForMessage() {
        log.info("Starting new thread to listen for messages");
        Thread listenerThread = new Thread(() -> {
            while (shouldKeepListening()) {
                try {
                    String messageFromServer = StreamReader.readPacket(ois);
                    log.debug("Message from server: {}", messageFromServer);
                    if (messageHandler != null && messageFromServer != null) {
                        try {
                            messageHandler.dispatch(new Message(messageFromServer));
                        } catch (Exception e) {
                            log.error("Error dispatching message: ", e);
                        }
                    } else {
                        log.warn("Received message but no handler is set");
                    }
                } catch (EOFException | SocketException e) {
                    log.warn("Connection closed: ", e);
                    break;
                } catch (Exception e) {
                    log.error("Error reading message from server in listener thread: ", e);
                    break;
                }
            }
            close();
        }, "ListenerThread");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    /**
     * Returns true if the connection is still active and the socket open.
     */
    private boolean shouldKeepListening() {
        return isActive.get() && socket != null && !socket.isClosed();
    }

    /**
     * Closes client connection by stopping the listener thread and closing streams and socket.
     */
    public final void close() {
        if (!isActive.getAndSet(false)) {
            return; // already closed
        }

        log.info("Closing client connection");
        try {
            if (oos != null) {
                oos.close();
            }
            if (ois != null) {
                ois.close();
            }
            if (socket != null) {
                socket.close();
            }
            log.info("ClientConnection closed successfully");
        } catch (IOException e) {
            log.error("Error closing resources: ", e);
        }
    }

    /**
     * Returns true if this connection has been closed or is inactive.
     *
     * @return true if inactive; false otherwise
     */
    public boolean isClosed() {
        return !isActive.get();
    }
}