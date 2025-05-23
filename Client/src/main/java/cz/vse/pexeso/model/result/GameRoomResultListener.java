package cz.vse.pexeso.model.result;

public interface GameRoomResultListener {
    void onGameRoomSuccess(Object data);

    void onGameRoomError(String errorDescription);

    void onPlayerUpdate(String data);

    void onGameRoomUIUpdate();
}
