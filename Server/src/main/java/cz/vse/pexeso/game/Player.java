package cz.vse.pexeso.game;

import cz.vse.pexeso.database.DatabaseController;
import cz.vse.pexeso.database.model.User;
import cz.vse.pexeso.main.Connection;

/**
 * Object representation of a player
 */
public class Player {

    private Connection conn;
    private boolean status;
    private DatabaseController dc;
    private int score;

    private User user;

    public Player(long playerId, Connection conn, DatabaseController dc) {
        this.conn = conn;
        this.status = false;
        this.dc = dc;

        this.score = 0;

        this.user = this.dc.getUserById(playerId);
    }

    /**
     * Returns status of player - before game start
     * @return boolean
     */
    public boolean isReady() {
        return this.status;
    }

    /**
     * Change player status to ready
     */
    public void setReady() {
        this.status = true;
    }

    /**
     * Sets player status to given boolean value
     * @param status Status to be set
     */
    public void setReady(boolean status) {
        this.status = status;
    }

    /**
     * Returns player connection for communication
     * @return Connection
     */
    public Connection getConnection() {
        return this.conn;
    }

    /**
     * Returns player ID
     * @return long
     */
    public long getPlayerId() {
        return this.user.id;
    }

    /**
     * Returns player name
     * @return String
     */
    public String getName() {
        return this.user.name;
    }

    /**
     * Returns player score
     * @return int
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Increments player score after guessing correctly
     */
    public void addPoint() {
        this.score += 1;
    }

}
