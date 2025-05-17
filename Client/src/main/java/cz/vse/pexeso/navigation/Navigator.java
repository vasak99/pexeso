package cz.vse.pexeso.navigation;

import javafx.stage.Stage;

public interface Navigator {
    void goToAuth();

    void goToLobby();

    void goToGame();

    Stage openGameRoomCreator();

    Stage openGameRoomManager();

    void showError(String message);

    boolean showConfirmation(String message);

    void closeWindow();
}
