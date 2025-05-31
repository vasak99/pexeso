package cz.vse.pexeso.model;

import cz.vse.pexeso.navigation.UIConstants;
import cz.vse.pexeso.view.Board;
import cz.vse.pexeso.view.GameCard;

import java.util.*;

public class Game {
    private Board gameBoard = null;
    private long activePlayer;
    private final Set<GameCard> currentTurn = new HashSet<>();
    private final Map<Long, String> playerColors = new HashMap<>();
    private final List<LobbyPlayer> resultList = new ArrayList<>();

    public Game() {
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(Board gameBoard) {
        this.gameBoard = gameBoard;
    }

    public long getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(long activePlayer) {
        this.activePlayer = activePlayer;
    }

    public List<LobbyPlayer> getResultList() {
        return resultList;
    }

    public Map<Long, String> getPlayerColors() {
        return playerColors;
    }

    public Set<GameCard> getCurrentTurn() {
        return currentTurn;
    }

    public void setupPlayerColors(List<LobbyPlayer> players) {
        for (int i = 0; i < players.size(); i++) {
            playerColors.put(players.get(i).getPlayerId(), UIConstants.getColors().get(i));
        }
    }
}
