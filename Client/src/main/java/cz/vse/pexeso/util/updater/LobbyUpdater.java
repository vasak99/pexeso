package cz.vse.pexeso.util.updater;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.SendableGame;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.util.GameRoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates the list of GameRoom instances in the lobby based on GameListPayload.
 * Removes deleted rooms and updates existing ones or adds new rooms as needed.
 *
 * @author kott10
 * @version June 2025
 */
public final class LobbyUpdater {
    private static final Logger log = LoggerFactory.getLogger(LobbyUpdater.class);

    private LobbyUpdater() {
    }

    /**
     * Synchronizes the local GameRoomManager.gameRooms list with the server payload.
     * Deletes any GameRoom not present in the payload, updates existing rooms, and adds new ones.
     *
     * @param glp payload containing a list of SendableGame instances
     */
    public static void update(GameListPayload glp) {
        log.info("Updating lobby with {} games from server", glp.games.size());

        // Remove deleted rooms
        GameRoomManager.gameRooms.removeIf(gameRoom -> hasGameRoomBeenDeleted(gameRoom, glp));

        // Update existing or add new
        for (SendableGame r : glp.games) {
            GameRoom existing = GameRoomManager.findById(r.id);
            if (existing != null) {
                existing.setName(r.name);
                existing.setStatus(GameRoom.GameStatus.fromBoolean(r.isStarted));
                existing.setCapacity(r.capacity);
                existing.setCardCount(r.cardCount);
                existing.setHostId(r.creatorId);
                existing.setHostName(r.creatorName);
                log.debug("Updated GameRoom[id={}]", r.id);
            } else {
                GameRoom newRoom = new GameRoom(
                        GameRoom.GameStatus.fromBoolean(r.isStarted),
                        r.id,
                        r.name,
                        r.creatorId,
                        r.creatorName,
                        r.capacity,
                        r.cardCount
                );
                GameRoomManager.gameRooms.add(newRoom);
                log.info("Added new GameRoom[id={}, name={}]", r.id, r.name);
            }
        }
    }

    /**
     * Helper to check if a game room should be deleted.
     */
    private static boolean hasGameRoomBeenDeleted(GameRoom gameRoom, GameListPayload glp) {
        for (SendableGame sendable : glp.games) {
            if (gameRoom.getGameId().equals(sendable.id)) {
                return false;
            }
        }
        log.debug("Deleting GameRoom[id={}]", gameRoom.getGameId());
        return true;
    }
}
