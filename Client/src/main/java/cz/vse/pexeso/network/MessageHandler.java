package cz.vse.pexeso.network;

import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.model.RedirectParameters;
import cz.vse.pexeso.model.observer.Observable;
import cz.vse.pexeso.model.observer.Observer;
import cz.vse.pexeso.model.observer.ObserverWithData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Responsible for parsing raw messages from the server and verifying the message data
 * Notifies registered observers based on the MessageType.
 *
 * @author kott10
 * @version June 2025
 */
public class MessageHandler implements Observable {
    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);
    private final Map<MessageType, Set<Observer>> simpleObservers = new ConcurrentHashMap<>();
    private final Map<MessageType, Set<ObserverWithData>> dataObservers = new ConcurrentHashMap<>();

    private static final Set<MessageType> MESSAGE_TYPES_REQUIRING_DATA = EnumSet.of(
            MessageType.IDENTITY, MessageType.GAME_SERVER_UPDATE, MessageType.REDIRECT,
            MessageType.LOBBY_UPDATE, MessageType.START_GAME, MessageType.INVALID_MOVE,
            MessageType.GAME_UPDATE, MessageType.ERROR
    );

    /**
     * Constructs a MessageHandler by initializing observer sets.
     */
    public MessageHandler() {
        for (MessageType type : MessageType.values()) {
            simpleObservers.put(type, new CopyOnWriteArraySet<>());
            dataObservers.put(type, new CopyOnWriteArraySet<>());
        }
        log.info("MessageHandler initialized with {} message types", MessageType.values().length);
    }

    /**
     * Verifies message, then dispatches to observers based on type.
     *
     * @param msg Message from server
     * @throws DataFormatException if message or data do not have the correct format
     */
    public void dispatch(Message msg) throws DataFormatException {
        if (msg == null) {
            throw new DataFormatException("Message is null");
        }

        MessageType messageType = msg.getType();
        if (messageType == null) {
            throw new DataFormatException("Message does not have a type.");
        }

        String data = msg.getData();
        if (MESSAGE_TYPES_REQUIRING_DATA.contains(messageType) && (data == null || data.isBlank())) {
            throw new DataFormatException("Missing or blank data for message type: " + messageType);
        }

        log.info("Dispatching message with type: {}", messageType);
        try {
            switch (messageType) {
                case IDENTITY -> handleIdentityMessage(data);
                case GAME_SERVER_UPDATE -> handleGameServerUpdateMessage(data);
                case REDIRECT -> handleRedirectMessage(data);
                case REQUEST_IDENTITY -> notifyObservers(MessageType.REQUEST_IDENTITY);
                case LOBBY_UPDATE -> handleLobbyUpdateMessage(data);
                case START_GAME -> handleStartGameMessage(data);
                case INVALID_MOVE -> notifyObservers(MessageType.INVALID_MOVE, data);
                case GAME_UPDATE -> handleGameUpdateMessage(data);
                case RESULT -> notifyObservers(MessageType.RESULT);
                case ERROR -> notifyObservers(MessageType.ERROR, data);
                default -> log.error("Unknown message type: {}", msg.getType());
            }
        } catch (DataFormatException e) {
            log.warn("Invalid format for type {}: {}", messageType, e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new DataFormatException("Unexpected error during message dispatch: " + e.getMessage(), e);
        }
    }

    /**
     * Handles the IDENTITY message type by parsing the player ID and notifying observers.
     *
     * @param data the data string containing the player ID
     * @throws DataFormatException if the data is not a valid long
     */
    private void handleIdentityMessage(String data) throws DataFormatException {
        long playerId;
        try {
            playerId = Long.parseLong(data);
        } catch (NumberFormatException e) {
            throw new DataFormatException("Invalid id");
        }
        notifyObservers(MessageType.IDENTITY, playerId);
    }

    /**
     * Handles the GAME_SERVER_UPDATE message type by parsing the game room list payload
     * and notifying observers with the game list.
     *
     * @param data the data string containing the game room list information
     * @throws DataFormatException if the data is not in the expected format
     */
    private void handleGameServerUpdateMessage(String data) throws DataFormatException {
        GameListPayload glp = new GameListPayload(data);
        if (glp.games == null) {
            throw new DataFormatException("Invalid GameListPayload data");
        }

        notifyObservers(MessageType.GAME_SERVER_UPDATE, glp);
    }

    /**
     * Handles the REDIRECT message type by parsing the redirect parameters
     * and notifying observers with the redirect data.
     *
     * @param data the data string containing redirect information
     * @throws DataFormatException if the data is not in the expected format
     */
    private void handleRedirectMessage(String data) throws DataFormatException {
        RedirectParameters parameters = RedirectService.parseRedirectData(data);
        if (parameters == null) {
            throw new DataFormatException("Invalid redirect data");
        }

        notifyObservers(MessageType.REDIRECT, parameters);
    }

    /**
     * Handles the LOBBY_UPDATE message type by parsing the lobby update payload
     * and notifying observers with the updated player list.
     *
     * @param data the data string containing lobby update information
     * @throws DataFormatException if the data is not in the expected format
     */
    private void handleLobbyUpdateMessage(String data) throws DataFormatException {
        LobbyUpdatePayload lup = new LobbyUpdatePayload(data);
        if (lup.players == null) {
            throw new DataFormatException("Invalid LobbyUpdatePayload data");
        }

        notifyObservers(MessageType.LOBBY_UPDATE, lup);
    }

    /**
     * Handles the START_GAME message type by parsing the game update payload
     * and notifying observers that the game has started with the payload.
     *
     * @param data the data string containing game board information
     * @throws DataFormatException if the data is not in the expected format
     */
    private void handleStartGameMessage(String data) throws DataFormatException {
        GameUpdatePayload gup = new GameUpdatePayload(data);
        if (gup.gameBoard == null) {
            throw new DataFormatException("Invalid GameUpdatePayload data");
        }

        notifyObservers(MessageType.START_GAME, gup);
    }

    /**
     * Handles the GAME_UPDATE message type by parsing the game update payload
     * and notifying observers with the updated game.
     *
     * @param data the data string containing game board information
     * @throws DataFormatException if the data is not in the expected format
     */
    private void handleGameUpdateMessage(String data) throws DataFormatException {
        GameUpdatePayload gup = new GameUpdatePayload(data);
        if (gup.gameBoard == null) {
            throw new DataFormatException("Invalid GameUpdatePayload data");
        }

        notifyObservers(MessageType.GAME_UPDATE, gup);
    }

    /**
     * Registers a simple observer for a given MessageType.
     *
     * @param type     the MessageType to observe
     * @param observer the observer instance
     */
    @Override
    public void register(MessageType type, Observer observer) {
        boolean added = simpleObservers.get(type).add(observer);
        log.debug("register {}: {}", type, added ? "SUCCESS" : "FAILED");
    }

    /**
     * Registers a data observer for a given MessageType.
     *
     * @param type             the MessageType to observe
     * @param observerWithData the observerWithData instance
     */
    @Override
    public void registerWithData(MessageType type, ObserverWithData observerWithData) {
        boolean added = dataObservers.get(type).add(observerWithData);
        log.debug("registerWithData {}: {}", type, added ? "SUCCESS" : "FAILED");
    }

    /**
     * Unregisters a simple observer for a given MessageType.
     *
     * @param type     the MessageType
     * @param observer the observer instance
     */
    @Override
    public void unregister(MessageType type, Observer observer) {
        boolean removed = simpleObservers.get(type).remove(observer);
        log.debug("unregister {}: {}", type, removed ? "SUCCESS" : "FAILED");
    }

    /**
     * Unregisters a data observer for a given MessageType.
     *
     * @param type             the MessageType
     * @param observerWithData the observerWithData instance
     */
    @Override
    public void unregisterWithData(MessageType type, ObserverWithData observerWithData) {
        boolean removed = dataObservers.get(type).remove(observerWithData);
        log.debug("unregisterWithData {}: {}", type, removed ? "SUCCESS" : "FAILED");
    }

    /**
     * Notifies all simple observers of the given MessageType.
     *
     * @param type the MessageType to notify
     */
    @Override
    public void notifyObservers(MessageType type) {
        for (Observer o : simpleObservers.get(type)) {
            o.update();
        }
    }

    /**
     * Notifies all data observers of the given MessageType with the provided data.
     *
     * @param type the MessageType to notify
     * @param data the data to pass to observers
     */
    @Override
    public void notifyObservers(MessageType type, Object data) {
        for (ObserverWithData o : dataObservers.get(type)) {
            o.update(data);
        }
    }
}