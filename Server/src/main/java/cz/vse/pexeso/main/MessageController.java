package cz.vse.pexeso.main;

import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.GameStatsPayload;
import cz.vse.pexeso.common.message.payload.JoinGamePayload;
import cz.vse.pexeso.common.message.payload.LoginPayload;
import cz.vse.pexeso.common.message.payload.RegisterPayload;
import cz.vse.pexeso.database.DatabaseController;
import cz.vse.pexeso.database.model.User;
import cz.vse.pexeso.game.Game;
import cz.vse.pexeso.utils.Utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles main client - server communication
 */
public class MessageController {

    private DatabaseController dc;
    private GameServerRuntime gsr;

    public MessageController(GameServerRuntime gsr, DatabaseController db) {
        this.dc = db;
        this.gsr = gsr;
    }

    /**
     * Delegates message handling based on message type
     * @param conn Player connection for sending responses
     * @param msg Received message with data
     */
    public void handleMessage(Connection conn, Message msg) {
        switch(msg.getType()) {
            case MessageType.CREATE_GAME:
                this.gsr.createGame(conn, msg);
                break;
            case MessageType.LOGIN:
                this.login(conn, msg);
                break;
            case MessageType.REGISTER:
                this.register(conn, msg);
                break;
            case MessageType.JOIN_GAME:
                this.joinGame(conn, msg);
                break;
            case MessageType.PLAYER_STATS:
                this.playerStats(conn, msg);
                break;
            default:
                break;
        }
    }

    /**
     * Handles login messages
     * @param conn Player connection for sending responses
     * @param msg Received message with data
     */
    private void login(Connection conn, Message msg) {
        try {
            LoginPayload data = new LoginPayload(msg.getData());

            java.sql.Connection db = this.dc.getDbConnection();
            PreparedStatement statement = db.prepareStatement("select * from users where name = ?;");
            statement.setString(1, data.username);

            var result = User.fromResultSet(statement.executeQuery());
            if(result.size() < 1) {
                conn.sendMessage(MessageFactory.getError("User with specified name not found").toSendable());
                return;
            }
            if(result.size() > 2) {
                conn.sendMessage(MessageFactory.getError("Duplicate identifier").toSendable());
                return;
            }

            if(result.get(0).password.equals(data.password)) {
                conn.sendMessage(MessageFactory.getIdentityMessage("" + result.get(0).id).toSendable());
            } else {
                conn.sendMessage(MessageFactory.getError("Login unsuccessful: incorrect password").toSendable());
            }

        } catch (DataFormatException e) {
            conn.sendMessage(MessageFactory.getError(e.getMessage()).toSendable());
        } catch (SQLException e) {
            conn.sendMessage(MessageFactory.getError(e.getMessage()).toSendable());
        }
    }

    /**
     * Handles register messages
     * @param conn Player connection for sending responses
     * @param msg Received message with data
     */
    private void register(Connection conn, Message msg) {
        try {
            RegisterPayload data = new RegisterPayload(msg.getData());

            var statement = this.dc.getDbConnection().prepareStatement("insert into users(name, password) values (?, ?) returning id;");
            statement.setString(1, data.username);
            statement.setString(2, data.password);

            var result = statement.executeQuery();
            if(result.next()) {
                long id = result.getLong("id");
                conn.sendMessage(MessageFactory.getIdentityMessage("" + id).toSendable());
            } else {
                conn.sendMessage(MessageFactory.getError("Unable to create new user").toSendable());
            }

        } catch (cz.vse.pexeso.common.exceptions.DataFormatException e) {
            conn.sendMessage(MessageFactory.getError(e.getMessage()).toSendable());
        } catch (SQLException e) {
            conn.sendMessage(MessageFactory.getError(e.getMessage()).toSendable());
        }
    }

    /**
     * Handles join game messages
     * @param conn Player connection for sending responses
     * @param msg Received message with data
     */
    private void joinGame(Connection conn, Message msg) {
        JoinGamePayload data = new JoinGamePayload(msg);
        Game game = this.gsr.getGameById(data.gameId);

        if(game == null) {
            conn.sendMessage(MessageFactory.getError("Game not found").toSendable());
            return;
        }

        if(game.isFull()) {
            conn.sendMessage(MessageFactory.getError("Game is full").toSendable());
            return;
        }

        String host = Utils.getLocalAddress();

        if("localhost".equals(host)) {
            conn.sendMessage(MessageFactory.getError("Could not load server address").toSendable());
            return;
        }

        conn.sendMessage(MessageFactory.getRedirectMessage(host, game.getPort()).toSendable());
    }

    /**
     * Handles player statistics request
     * @param conn Player connection for sending responses
     * @param msg Received message with data
     */
    private void playerStats(Connection conn, Message msg) {
        var stats = this.dc.getAllPlayerGames(msg.getPlayerId());

        GameStatsPayload ret = new GameStatsPayload(stats);
        conn.sendMessage(MessageFactory.getStatsMessage(ret).toSendable());
    }

}
