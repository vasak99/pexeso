package cz.vse.pexeso.util;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.common.message.payload.SendableGame;
import cz.vse.pexeso.common.message.payload.SendablePlayer;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;

public final class Updater {

    public static void updateLobby(GameListPayload glp) {
        GameRoom.gameRooms.removeIf(gameRoom -> hasBeenDeleted(gameRoom, glp));

        for (SendableGame r : glp.games) {
            GameRoom gameRoom = GameRoom.findById(r.id);
            if (gameRoom != null) {
                gameRoom.setName(r.name);
                gameRoom.setStatus(GameRoom.GameStatus.fromBoolean(r.isStarted));
                gameRoom.setCapacity(r.capacity);
                gameRoom.setCardCount(r.cardCount);
                gameRoom.setHostId(r.creatorId);
                gameRoom.setHostName(r.creatorName);
            } else {
                GameRoom.gameRooms.add(new GameRoom(GameRoom.GameStatus.fromBoolean(r.isStarted), r.id, r.name, r.creatorId, r.creatorName, r.capacity, r.cardCount));
            }
        }
    }

    private static boolean hasBeenDeleted(GameRoom gameRoom, GameListPayload glp) {
        for (SendableGame sendableGameRoom : glp.games) {
            if (gameRoom.getGameId().equals(sendableGameRoom.id)) {
                return false;
            }
        }
        return true;
    }

    public static void updateGameRoom(GameRoom gameRoom, LobbyUpdatePayload lup) {
        GameRoom.editGameRoom(gameRoom.getGameId(), lup.name, lup.playersCapacity, lup.cardCount);

        gameRoom.getPlayers().clear();

        for (SendablePlayer sendablePlayer : lup.players) {
            gameRoom.getPlayers().add(new LobbyPlayer(sendablePlayer.id, sendablePlayer.name, LobbyPlayer.PlayerStatus.fromBoolean(sendablePlayer.status), sendablePlayer.score));
        }
    }
}
