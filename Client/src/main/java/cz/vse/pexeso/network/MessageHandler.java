package cz.vse.pexeso.network;

import cz.vse.pexeso.model.observer.MessageType;
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
    private final Map<MessageType, Set<Observer>> listOfObservers = new HashMap<>();


    public MessageHandler() {
        for (MessageType messageType : MessageType.values()) {
            listOfObservers.put(messageType, new HashSet<>());
        }
    }

    /**
     * Parses the message and dispatches it to the appropriate handler based on the message type.
     *
     * @param message The message received from the server.
     */
    public void parseMessage(String message) {
        log.debug("Parsing message: {}", message);
        String[] messageParts = message.split("\\|");
        String messageType = messageParts[0];

        switch (messageType) {
            case "LOGIN" -> handleLoginResponse(messageParts);
            case "CARD_PAIR" -> handleCardPairResponse(messageParts);
        }
    }

    private void handleLoginResponse(String[] messageParts) {
        log.info("Handling login response.");
        String messageBody = messageParts[1];
        switch (messageBody) {
            case "OK": {
                notifyObserver(MessageType.LOGIN_OK);
                break;
            }
            case "INVALID": {
                notifyObserver(MessageType.LOGIN_INVALID);
                break;
            }
            case "DUPLICATE": {
                notifyObserver(MessageType.LOGIN_DUPLICATE);
                break;
            }
        }
    }

    private void handleCardPairResponse(String[] messageParts) {
        log.info("Handling card pair response.");
        String messageBody = messageParts[1];
        switch (messageBody) {
            case "OK": {
                notifyObserver(MessageType.CARD_PAIR_OK);
                break;
            }
            case "INVALID": {
                notifyObserver(MessageType.CARD_PAIR_INVALID);
                break;
            }
        }
    }

    @Override
    public void register(MessageType messageType, Observer observer) {
        listOfObservers.get(messageType).add(observer);
    }

    private void notifyObserver(MessageType messageType) {
        for (Observer observer : listOfObservers.get(messageType)) {
            observer.update();
        }
    }
}
