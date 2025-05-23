package cz.vse.pexeso.model.result;

public interface LobbyResultListener {
    void onLobbySuccess(String gameId);

    void onLobbyError(String errorDescription);

    void onGameRoomUpdate(String data);

    void onPlayerUpdate(String data);

    void onLobbyUIUpdate();

    void onIdentityRequested();

    void onStartGame();
}
