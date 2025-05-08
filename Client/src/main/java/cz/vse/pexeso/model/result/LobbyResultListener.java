package cz.vse.pexeso.model.result;

public interface LobbyResultListener {
    void onLobbySuccess(String gameId);

    void onLobbyError(String errorDescription);

    void onLobbyUpdate();
}
