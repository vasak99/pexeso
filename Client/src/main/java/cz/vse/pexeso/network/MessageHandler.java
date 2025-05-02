package cz.vse.pexeso.network;

import cz.vse.pexeso.common.message.Message;
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
    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    private final Map<MessageTypeClient, Set<Observer>> simpleObservers = new HashMap<>();
    private final Map<MessageTypeClient, Set<ObserverWithData>> dataObservers = new HashMap<>();

    public MessageHandler() {
        for (MessageTypeClient type : MessageTypeClient.values()) {
            simpleObservers.put(type, new HashSet<>());
            dataObservers.put(type, new HashSet<>());
        }
    }

    public void dispatch(String message) {
        log.debug("Parsing message: {}", message);
        Message msg = new Message(message);

        switch (msg.getType()) {
            case MessageType.IDENTITY -> handleIdentityMessage(msg);
            case MessageType.REDIRECT, MessageType.EDIT_GAME, MessageType.DELETE_GAME -> handleRoomChangeMessage(msg);
            case MessageType.ERROR -> handleErrorMessage(msg);
            default -> log.error("Unknown message type: {}", msg.getType());
        }
    }

    private void handleIdentityMessage(Message msg) {
        long playerId = Long.parseLong(msg.getData());
        notifyObservers(MessageTypeClient.AUTH_SUCCESS, playerId);
    }

    private void handleRoomChangeMessage(Message msg) {
        log.info("Handling redirect message.");
        String redirectData = msg.getData();

        notifyObservers(MessageTypeClient.GAME_ROOM_SUCCESS, redirectData);
        notifyObservers(MessageTypeClient.GAME_TABLE_CHANGE);
    }

    private void handleErrorMessage(Message msg) {
        log.info("Handling error message.");
        String errorDescription = msg.getData();
        notifyObservers(MessageTypeClient.ERROR, errorDescription);
    }

    @Override
    public void register(MessageTypeClient type, Observer observer) {
        boolean added = simpleObservers.get(type).add(observer);
        log.debug("register {}: {}", type, added ? "SUCCESS" : "FAILED");
    }

    @Override
    public void registerWithData(MessageTypeClient type, ObserverWithData observerWithData) {
        boolean added = dataObservers.get(type).add(observerWithData);
        log.debug("registerWithData {}: {}", type, added ? "SUCCESS" : "FAILED");
    }

    @Override
    public void unregister(MessageTypeClient type, Observer observer) {
        boolean removed = simpleObservers.get(type).remove(observer);
        log.debug("unregister {}: {}", type, removed ? "SUCCESS" : "FAILED");
    }

    @Override
    public void unregisterWithData(MessageTypeClient type, ObserverWithData observerWithData) {
        boolean removed = dataObservers.get(type).remove(observerWithData);
        log.debug("unregisterWithData {}: {}", type, removed ? "SUCCESS" : "FAILED");
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
