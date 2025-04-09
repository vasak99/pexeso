package cz.vse.pexeso.network;

import cz.vse.pexeso.helper.AppServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class ClientConnection {
    public static final Logger log = LoggerFactory.getLogger(ClientConnection.class);
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

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

    public void listenForMessage() {
        log.info("Starting new thread to listen for messages");
        new Thread(() -> {
            String messageFromServer;

            while (socket.isConnected()) {
                try {
                    messageFromServer = bufferedReader.readLine();
                    log.debug("Message from server: {}", messageFromServer);
                    AppServices.getMessageHandler().parseMessage(messageFromServer);
                } catch (IOException e) {
                    log.error("Error reading message from server in listener thread: ", e);
                    closeResources(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

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

