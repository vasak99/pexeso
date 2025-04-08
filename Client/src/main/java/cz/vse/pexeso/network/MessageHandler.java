package cz.vse.pexeso.network;

import cz.vse.pexeso.model.observer.MessageType;
import cz.vse.pexeso.model.observer.Observable;
import cz.vse.pexeso.model.observer.Observer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageHandler implements Observable {

    private final Map<MessageType, Set<Observer>> listOfObservers = new HashMap<>();

    public MessageHandler() {
        for (MessageType messageType : MessageType.values()) {
            listOfObservers.put(messageType, new HashSet<>());
        }
    }

    public void parseMessage(String message) {
        String[] messageParts = message.split("\\|");
        String messageType = messageParts[0];

        switch (messageType) {
            case "LOGIN" -> handleLoginResponse(messageParts);
        }
    }

    private void handleLoginResponse(String[] messageParts) {
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
