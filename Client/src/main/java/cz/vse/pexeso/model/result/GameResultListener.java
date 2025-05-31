package cz.vse.pexeso.model.result;

public interface GameResultListener {
    void onInvalidMove(String errorDescription);

    void onGameUpdate(String data);

    void onGameResult(String data);

    void onGameError(String errorDescription);

    void onRedirect(String redirectData);

    void onGameRoomUpdate(String data);
}
