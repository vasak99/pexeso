package cz.vse.pexeso.navigation;

import javafx.stage.Stage;

public class SceneNavigator implements Navigator {
    private final SceneManager sceneManager;

    public SceneNavigator(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @Override
    public void goToAuth() {
        sceneManager.switchScene(UIConstants.AUTH_FXML);
    }

    @Override
    public void goToLobby() {
        sceneManager.switchScene(UIConstants.LOBBY_FXML);
    }

    @Override
    public void goToGame() {
        sceneManager.switchScene(UIConstants.GAME_FXML);
    }

    @Override
    public Stage openGameRoomCreator() {
        return sceneManager.openWindow(UIConstants.GAME_ROOM_FORM_FXML, "Create game room");
    }

    @Override
    public Stage openGameRoomManager() {
        return sceneManager.openWindow(UIConstants.GAME_ROOM_MANAGER_FXML, "Manage game room");
    }

    @Override
    public void showError(String message) {
        sceneManager.showErrorAlert(message);
    }

    @Override
    public boolean showConfirmation(String message) {
        return sceneManager.showConfirmationAlert(message);
    }

    @Override
    public void closeWindow() {
        sceneManager.closeWindow();
    }
}
