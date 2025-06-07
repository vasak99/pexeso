package cz.vse.pexeso.util;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.util.updater.LobbyUpdater;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the current list of available game rooms.
 *
 * @author kott10
 * @version June 2025
 */
public final class GameRoomManager {

    private static final Logger log = LoggerFactory.getLogger(GameRoomManager.class);

    private GameRoomManager() {
    }

    /**
     * Shared observable list of available game rooms.
     */
    public static final ObservableList<GameRoom> gameRooms = FXCollections.observableArrayList();

    /**
     * Edits an existing game room's name, capacity, and card count.
     *
     * @param gameId    the ID of the room to edit
     * @param name      new name for the room
     * @param capacity  new maximum number of players
     * @param cardCount new number of cards in the game
     */
    public static void editGameRoom(String gameId, String name, int capacity, int cardCount) {
        for (GameRoom gameRoom : gameRooms) {
            if (gameRoom.getGameId().equals(gameId)) {
                log.debug("Editing game room '{}' → name: '{}', capacity: {}, cardCount: {}",
                        gameId, name, capacity, cardCount);

                gameRoom.setName(name);
                gameRoom.setCapacity(capacity);
                gameRoom.setCardCount(cardCount);
                return;
            }
        }

        log.warn("Attempted to edit non-existent game room with gameId='{}'", gameId);
    }

    /**
     * Finds a game room by its ID.
     *
     * @param gameId the ID of the room to find
     * @return the GameRoom if found, or null otherwise
     */
    public static GameRoom findById(String gameId) {
        for (GameRoom gameRoom : gameRooms) {
            if (gameRoom.getGameId().equals(gameId)) {
                return gameRoom;
            }
        }

        log.debug("No game room found with gameId='{}'", gameId);
        return null;
    }

    /**
     * Updates the game room list using payload.
     *
     * @param data game list payload
     */
    public static void update(GameListPayload data) {
        log.debug("Updating game room list from payload");
        LobbyUpdater.update(data);
    }
}