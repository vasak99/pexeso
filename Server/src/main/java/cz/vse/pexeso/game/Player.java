package cz.vse.pexeso.game;

import cz.vse.pexeso.database.DatabaseController;
import cz.vse.pexeso.database.model.User;
import cz.vse.pexeso.main.Connection;

public class Player {

    private Connection conn;
    private boolean status;
    private DatabaseController dc;
    private int score;

    private User user;

    public Player(String playerId, Connection conn, DatabaseController dc) {
        this.conn = conn;
        this.status = false;
        this.dc = dc;

        this.score = 0;

        this.user = this.dc.getUserById(playerId);
    }

    public boolean isReady() {
        return this.status;
    }

    public void setReady() {
        this.status = true;
    }

    public void setReady(boolean status) {
        this.status = status;
    }

    public Connection getConnection() {
        return this.conn;
    }

    public String getPlayerId() {
        return "" + this.user.id;
    }

    public String getName() {
        return this.user.name;
    }

    public int getScore() {
        return this.score;
    }

    public void addPoint() {
        this.score += 1;
    }

}
