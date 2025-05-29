package cz.vse.pexeso.util;

import cz.vse.pexeso.common.message.payload.*;
import cz.vse.pexeso.model.GameRoom;
import cz.vse.pexeso.model.LobbyPlayer;
import cz.vse.pexeso.view.Board;

import java.util.Comparator;
import java.util.List;

public final class Updater {

    public static void updateLobby(GameListPayload glp) {
        GameRoom.gameRooms.removeIf(gameRoom -> hasGameRoomBeenDeleted(gameRoom, glp));

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

    public static void updateGameRoom(GameRoom gameRoom, LobbyUpdatePayload lup) {
        GameRoom.editGameRoom(gameRoom.getGameId(), lup.name, lup.playersCapacity, lup.cardCount);

        updateGameRoomPlayers(gameRoom, lup.players);
    }

    public static void updateGame(GameRoom gameRoom, GameUpdatePayload gup) {
        if (gameRoom.getGame().getGameBoard() == null) {
            gameRoom.getGame().setGameBoard(new Board(gup.gameBoard));
        } else {
            gameRoom.getGame().getGameBoard().setGameBoardString(gup.gameBoard, gameRoom.getGame().getPlayerColors().get(gameRoom.getGame().getActivePlayer()));
        }

        if (gameRoom.getGame().getActivePlayer() != gup.activePlayer) {
            gameRoom.getGame().setActivePlayer(gup.activePlayer);
        }

        updateGameRoomPlayers(gameRoom, gup.players);
    }

    private static void updateGameRoomPlayers(GameRoom gameRoom, List<SendablePlayer> players) {
        gameRoom.getPlayers().clear();

        for (SendablePlayer sendablePlayer : players) {
            gameRoom.getPlayers().add(new LobbyPlayer(sendablePlayer.id, sendablePlayer.name, LobbyPlayer.PlayerStatus.fromBoolean(sendablePlayer.status), sendablePlayer.score));
        }
    }

    private static boolean hasGameRoomBeenDeleted(GameRoom gameRoom, GameListPayload glp) {
        for (SendableGame sendableGameRoom : glp.games) {
            if (gameRoom.getGameId().equals(sendableGameRoom.id)) {
                return false;
            }
        }
        return true;
    }

    public static void setResult(GameRoom gameRoom, ResultPayload rp) {
        gameRoom.getGame().getResultList().clear();
        for (SendablePlayer sendablePlayer : rp.scores) {
            gameRoom.getGame().getResultList().add(new LobbyPlayer(sendablePlayer.name, sendablePlayer.score));
        }
        gameRoom.getGame().getResultList().sort(Comparator.comparingInt(LobbyPlayer::getScore).reversed());
    }
}
