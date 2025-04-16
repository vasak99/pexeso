package cz.vse.pexeso.network;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageTranslatorImpl;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.Observable;
import cz.vse.pexeso.model.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for parsing messages received from the server and notifying the appropriate observers.
 */
public class MessageHandler implements Observable {

    public static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    private final Map<MessageTypeClient, Set<Observer>> listOfObservers = new HashMap<>();
    private final MessageTranslatorImpl messageTranslator = new MessageTranslatorImpl();


    public MessageHandler() {
        for (MessageTypeClient messageTypeClient : MessageTypeClient.values()) {
            listOfObservers.put(messageTypeClient, new HashSet<>());
        }
    }

    /**
     * Translates the message and dispatches it to the appropriate handler based on the message type.
     *
     * @param message The message received from the server.
     */
    public void dispatch(String message) {
        log.debug("Parsing message: {}", message);
        Message msg = messageTranslator.stringToMessage(message);

        switch (msg.getType()) {
            case MessageType.LOGIN -> handleLoginMessage(msg.getData());
            case MessageType.REVEAL -> handleRevealMessage(msg.getData());
        }
    }

    private void handleLoginMessage(String data) {
        log.info("Handling login message.");
        switch (data) {
            case "OK": {
                notifyObserver(MessageTypeClient.LOGIN_OK);
                break;
            }
            case "INVALID": {
                notifyObserver(MessageTypeClient.LOGIN_INVALID);
                break;
            }
            case "DUPLICATE": {
                notifyObserver(MessageTypeClient.LOGIN_DUPLICATE);
                break;
            }
        }
    }

    private void handleRevealMessage(String data) {
        log.info("Handling reveal message.");
        switch (data) {}

    }

    @Override
    public void register(MessageTypeClient messageTypeClient, Observer observer) {
        listOfObservers.get(messageTypeClient).add(observer);
    }

    private void notifyObserver(MessageTypeClient messageTypeClient) {
        for (Observer observer : listOfObservers.get(messageTypeClient)) {
            observer.update();
        }
    }
}
