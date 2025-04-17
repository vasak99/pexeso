package cz.vse.pexeso.network;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageTranslatorImpl;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.Observable;
import cz.vse.pexeso.model.observer.Observer;
import cz.vse.pexeso.model.observer.ObserverWithData;
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
    private final Map<MessageTypeClient, Set<Observer>> simpleObservers = new HashMap<>();
    private final Map<MessageTypeClient, Set<ObserverWithData>> dataObservers = new HashMap<>();
    private final MessageTranslatorImpl messageTranslator = new MessageTranslatorImpl();


    public MessageHandler() {
        for (MessageTypeClient type : MessageTypeClient.values()) {
            simpleObservers.put(type, new HashSet<>());
            dataObservers.put(type, new HashSet<>());
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
            case MessageType.STATUS -> handleStatusMessage(msg.getData());
            case MessageType.MOVE -> handleMoveMessage(msg.getData());
            case MessageType.REDIRECT -> handleRedirectMessage(msg.getData());
            case MessageType.RESULT -> handleResultMessage(msg.getData());
            case MessageType.ERROR -> handleErrorMessage(msg.getData());
        }
    }

    private void handleLoginMessage(String playerId) {
        log.info("Handling login message.");
        notifyObservers(MessageTypeClient.LOGIN, playerId);
    }

    private void handleRevealMessage(String data) {
        log.info("Handling reveal message.");
    }

    private void handleStatusMessage(String data) {
        log.info("Handling status message.");
    }

    private void handleMoveMessage(String data) {
        log.info("Handling move message.");
    }

    private void handleRedirectMessage(String data) {
        log.info("Handling redirect message.");
    }

    private void handleResultMessage(String data) {
        log.info("Handling result message.");
    }

    private void handleErrorMessage(String errorDescription) {
        log.info("Handling error message.");


        // if error is related to login, then
        notifyObservers(MessageTypeClient.ERROR_LOGIN, errorDescription);
    }

    @Override
    public void register(MessageTypeClient type, Observer observer) {
        simpleObservers.get(type).add(observer);
    }

    @Override
    public void registerWithData(MessageTypeClient type, ObserverWithData observer) {
        dataObservers.get(type).add(observer);
    }

    @Override
    public void notifyObservers(MessageTypeClient type) {
        for (Observer o : simpleObservers.get(type)) {
            o.update();
        }
    }

    @Override
    public void notifyObservers(MessageTypeClient type, Object data) {
        for (ObserverWithData o : dataObservers.get(type)) {
            o.update(data);
        }
    }
}