package cz.vse.pexeso.network;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.Observable;
import cz.vse.pexeso.model.observer.Observer;
import cz.vse.pexeso.model.observer.ObserverWithData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Responsible for parsing messages received from the server and notifying the appropriate observers.
 */
public class MessageHandler implements Observable {
    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    private final Map<MessageTypeClient, Set<Observer>> simpleObservers = new ConcurrentHashMap<>();
    private final Map<MessageTypeClient, Set<ObserverWithData>> dataObservers = new ConcurrentHashMap<>();

    public MessageHandler() {
        for (MessageTypeClient type : MessageTypeClient.values()) {
            simpleObservers.put(type, new CopyOnWriteArraySet<>());
            dataObservers.put(type, new CopyOnWriteArraySet<>());
        }
    }

    public void dispatch(String message) {
        Message msg = new Message(message);
        String data = msg.getData();

        switch (msg.getType()) {
            case MessageType.IDENTITY -> handleIdentityMessage(Long.parseLong(data));
            case MessageType.REDIRECT -> handleRedirectMessage(data);
            case MessageType.REQUEST_IDENTITY -> handleRequestIdentityMessage();
            case MessageType.LOBBY_UPDATE -> handlePlayerUpdate(data);
            case MessageType.GAME_SERVER_UPDATE -> handleGameRoomUpdate(data);
            case MessageType.START_GAME -> handleStartGame(data);
            case MessageType.INVALID_MOVE -> handeInvalidMoveMessage(data);
            case MessageType.GAME_UPDATE -> handleGameUpdate(data);
            case MessageType.RESULT -> handleGameResult(data);
            case MessageType.ERROR -> handleErrorMessage(data);

            default -> log.error("Unknown message type: {}", msg.getType());
        }
    }

    private void handleIdentityMessage(long playerId) {
        notifyObservers(MessageTypeClient.AUTH_SUCCESS, playerId);
    }

    private void handleRedirectMessage(String redirectData) {
        log.info("Handling redirect message.");

        notifyObservers(MessageTypeClient.GAME_ROOM_SUCCESS, redirectData);
        notifyObservers(MessageTypeClient.LOBBY_UI_UPDATE);
        notifyObservers(MessageTypeClient.REDIRECT, redirectData);
    }

    private void handlePlayerUpdate(String data) {
        notifyObservers(MessageTypeClient.PLAYER_UPDATE, data);
        notifyObservers(MessageTypeClient.GAME_ROOM_UI_UPDATE);
    }

    private void handleGameRoomUpdate(String data) {
        notifyObservers(MessageTypeClient.GAME_ROOM_UPDATE, data);
    }

    private void handleRequestIdentityMessage() {
        notifyObservers(MessageTypeClient.IDENTITY_REQUESTED);
    }

    private void handleStartGame(String data) {
        notifyObservers(MessageTypeClient.START_GAME, data);
    }

    private void handeInvalidMoveMessage(String data) {
        notifyObservers(MessageTypeClient.INVALID_MOVE, data);
    }

    private void handleGameUpdate(String data) {
        notifyObservers(MessageTypeClient.GAME_UPDATE, data);
    }

    private void handleGameResult(String data) {
        notifyObservers(MessageTypeClient.GAME_RESULT, data);
    }

    private void handleErrorMessage(String errorDescription) {
        log.info("Handling error message.");
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
