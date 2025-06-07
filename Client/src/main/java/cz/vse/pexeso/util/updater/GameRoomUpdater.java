package cz.vse.pexeso.util.updater;

import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.common.message.payload.SendablePlayer;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.util.GameRoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Updates a specific GameRoom’s state and player list from a LobbyUpdatePayload.
 *
 * @author kott10
 * @version June 2025
 */
public final class GameRoomUpdater {
    private static final Logger log = LoggerFactory.getLogger(GameRoomUpdater.class);

    private GameRoomUpdater() {
    }

    /**
     * Updates the given GameRoom’s settings (name, capacity, cardCount),
     * then refreshes its player list according to the payload.
     *
     * @param gameRoom the GameRoom to update
     * @param lup      payload containing updated room state and player list
     */
    public static void update(GameRoom gameRoom, LobbyUpdatePayload lup) {
        GameRoomManager.editGameRoom(gameRoom.getGameId(), lup.name, lup.playersCapacity, lup.cardCount);
        log.info("GameRoom[id={}] metadata updated: name={}, capacity={}, cardCount={}",
                gameRoom.getGameId(), lup.name, lup.playersCapacity, lup.cardCount);

        updatePlayers(gameRoom, lup.players);
    }

    /**
     * Helper to convert SendablePlayers into LobbyPlayers and update the list.
     */
    private static void updatePlayers(GameRoom gameRoom, List<SendablePlayer> players) {
        List<LobbyPlayer> newPlayers = players.stream()
                .map(sp -> new LobbyPlayer(sp.id, sp.name, LobbyPlayer.PlayerStatus.fromBoolean(sp.status), sp.score))
                .toList();
        gameRoom.getPlayers().setAll(newPlayers);
        log.info("GameRoom[id={}] player list updated ({} players)", gameRoom.getGameId(), newPlayers.size());
    }
}