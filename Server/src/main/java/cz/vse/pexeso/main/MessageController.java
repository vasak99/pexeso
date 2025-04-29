package cz.vse.pexeso.main;

import cz.vse.pexeso.common.exceptions.DataFormatException;
import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.LoginPayload;
import cz.vse.pexeso.common.message.payload.RegisterPayload;
import cz.vse.pexeso.database.DatabaseController;
import cz.vse.pexeso.database.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class MessageController extends Messenger {

    private DatabaseController dc;
    private GameServerRuntime gsr;

    public MessageController(GameServerRuntime gsr, DatabaseController db) {
        this.dc = db;
        this.gsr = gsr;
    }

    public void handleMessage(Connection conn, Message msg) {
        switch(msg.getType()) {
            case MessageType.CREATE_GAME:
                this.gsr.createGame(conn, msg.getData());
                break;
            case MessageType.LOGIN:
                this.login(conn, msg);
                break;
            case MessageType.REGISTER:
                this.register(conn, msg);
                break;
            default:
                break;
        }
    }

    private void login(Connection conn, Message msg) {
        try {
            LoginPayload data = new LoginPayload(msg.getData());

            java.sql.Connection db = this.dc.getDbConnection();
            PreparedStatement statement = db.prepareStatement("select * from users where name = ?;");
            statement.setString(1, data.username);

            var result = User.fromResultSet(statement.executeQuery());
            if(result.size() < 1) {
                conn.sendMessage(getError("User with specified name not found").toSendable());
                return;
            }
            if(result.size() > 2) {
                conn.sendMessage(getError("Duplicate identifier").toSendable());
                return;
            }

            if(result.get(0).password.equals(data.password)) {
                conn.sendMessage(this.getIdentityMessage("" + result.get(0).id).toSendable());
            } else {
                conn.sendMessage(this.getError("Login unsuccessful: incorrect password").toSendable());
            }

        } catch (DataFormatException e) {
            conn.sendMessage(getError(e.getMessage()).toSendable());
        } catch (SQLException e) {
            conn.sendMessage(getError(e.getMessage()).toSendable());
        }
    }

    private void register(Connection conn, Message msg) {
        try {
            RegisterPayload data = new RegisterPayload(msg.getData());

            var statement = this.dc.getDbConnection().prepareStatement("insert into users(name, password) values (?, ?) returning id;");
            statement.setString(1, data.name);
            statement.setString(2, data.password);

            var result = statement.executeQuery();
            if(result.next()) {
                long id = result.getLong("id");
                conn.sendMessage(getIdentityMessage("" + id).toSendable());
            } else {
                conn.sendMessage(getError("Unable to create new user").toSendable());
            }

        } catch (cz.vse.pexeso.common.exceptions.DataFormatException e) {
            conn.sendMessage(getError(e.getMessage()).toSendable());
        } catch (SQLException e) {
            conn.sendMessage(getError(e.getMessage()).toSendable());
        }
    }

}
