package cz.vse.pexeso.model;

public class GameRoom {
    private String gameId;
    private String host;
    private String name;
    private String boardSize;
    private int maxPlayers;
    private String players;
    private String status;

    public GameRoom() {
    }

    public GameRoom(String gameId, String host, String name, String boardSize, int maxPlayers, String players, String status) {
        this.gameId = gameId;
        this.host = host;
        this.name = name;
        this.boardSize = boardSize;
        this.maxPlayers = maxPlayers;
        this.players = players;
        this.status = status;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(String boardSize) {
        this.boardSize = boardSize;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
