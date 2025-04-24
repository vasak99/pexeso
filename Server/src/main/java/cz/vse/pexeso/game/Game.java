package cz.vse.pexeso.game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.exceptions.PlayersException;
import cz.vse.pexeso.main.Connection;
import cz.vse.pexeso.utils.Observable;
import cz.vse.pexeso.utils.Observer;
import cz.vse.pexeso.exceptions.CardsException;

public class Game implements Observer {

    public static final Logger log = LoggerFactory.getLogger(Game.class);

    private GameBoard gameBoard;
    private ServerSocket serverSocket;

    private boolean isStarted = false;
    private String gameId;

    private int playersCapacity;
    private Map<String, Connection> connections;

    private Acceptor acceptor;

    public Game(int playersCapacity, int cardCount, int port) throws PlayersException, CardsException, IOException {
        if(playersCapacity < Variables.MIN_PLAYERS) {
            throw new PlayersException("Minimum number of players is " + Variables.MIN_PLAYERS);
        }
        if(playersCapacity > Variables.MAX_PLAYERS) {
            throw new PlayersException("Maximum number of players is " + Variables.MAX_PLAYERS);
        }

        this.gameBoard = new GameBoard(cardCount);

        this.connections = new HashMap<>();
        this.serverSocket = new ServerSocket(port);
        this.playersCapacity = playersCapacity;
    }

    public void startSession() {
        this.acceptor = new Acceptor(this.serverSocket, this.connections, this.playersCapacity, this);
        new Thread(this.acceptor).start();
    }

    public void startGame() {
        this.acceptor.terminate();
        this.isStarted = true;
    }

    private void sendTo(String player, Message message) {
        var conn = this.connections.get(player);
        if(conn != null) {
            conn.sendMessage(message.toSendable());
        }
    }

    public String getId() {
        return this.gameId;
    }

    @Override
    public void onNotify(Observable conn, Object o) {
        if(o instanceof Message) {
            Message msg = (Message) o;
            log.info(msg.getType().getValue());
        }
    }

}
