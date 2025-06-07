package cz.vse.pexeso.main;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.message.MessageType;
import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.GameStatsPayload;
import cz.vse.pexeso.common.message.payload.GameUpdatePayload;
import cz.vse.pexeso.common.message.payload.InvalidMovePayload;
import cz.vse.pexeso.common.message.payload.LobbyUpdatePayload;
import cz.vse.pexeso.common.message.payload.ResultPayload;

public final class MessageFactory {

    private MessageFactory() {}

    public static Message getError(String message) {
        Message ret = createMessage(MessageType.ERROR);
        ret.setData(message);
        return ret;
    }

    public static Message getLoginMessage(String data) {
        Message ret = createMessage(MessageType.LOGIN);
        ret.setData(data);
        return ret;
    }

    public static Message getRegisterMessage(String data) {
        Message ret = createMessage(MessageType.REGISTER);
        ret.setData(data);
        return ret;
    }

    public static Message getIdentityMessage(String data) {
        Message ret = createMessage(MessageType.IDENTITY);
        ret.setData(data);
        return ret;
    }

    public static Message getIdentityRequest(String gameId) {
        Message ret = createMessage(MessageType.REQUEST_IDENTITY);
        ret.setData(gameId);
        return ret;
    }

    public static Message getGameStartMessage(String data) {
        Message ret = createMessage(MessageType.START_GAME);
        ret.setData(data);
        return ret;
    }

    public static Message getRedirectMessage(String host, String port) {
        Message ret = createMessage(MessageType.REDIRECT);
        ret.setData(host + ":" + port);
        return ret;
    }

    public static Message getRedirectMessage(String host, int port) {
        Message ret = createMessage(MessageType.REDIRECT);
        ret.setData(host + ":" + port);
        return ret;
    }

    public static Message getLobbyMessage(LobbyUpdatePayload data) {
        Message ret = createMessage(MessageType.LOBBY_UPDATE);
        ret.setData(data.toSendable());
        return ret;

    }

    public static Message getGsrUpdateMessage(GameListPayload data) {
        Message ret = createMessage(MessageType.GAME_SERVER_UPDATE);
        ret.setData(data.toSendable());
        return ret;
    }

    public static Message getGameUpdateMessage(GameUpdatePayload data) {
        Message ret = createMessage(MessageType.GAME_UPDATE);
        ret.setData(data.toSendable());
        return ret;
    }

    public static Message getInvalidMoveMessage(InvalidMovePayload data) {
        Message ret = createMessage(MessageType.INVALID_MOVE);
        ret.setData(data.toSendable());
        return ret;
    }

    public static Message getInvalidMoveMessage(String message) {
        Message ret = createMessage(MessageType.INVALID_MOVE);
        InvalidMovePayload dd = new InvalidMovePayload(message);
        ret.setData(dd.toSendable());
        return ret;
    }

    public static Message getResultMessage(ResultPayload data) {
        Message ret = createMessage(MessageType.RESULT);
        ret.setData(data.toSendable());
        return ret;
    }

    public static Message getStatsMessage(GameStatsPayload stats) {
        Message ret = createMessage(MessageType.PLAYER_STATS);
        ret.setData(stats.toSendable());
        return ret;
    }

    private static Message createMessage(MessageType type) {
        Message msg = new Message();
        msg.setType(type);
        return msg;
    }

}
