package cz.vse.pexeso.navigation;

import javafx.stage.Stage;

/**
 * Implements Navigator.
 * Provides methods to switch scenes and open dialogs/windows for the Pexeso client.
 *
 * @author kott10
 * @version June 2025
 */
public class SceneNavigator implements Navigator {

    private final SceneManager sceneManager;

    public SceneNavigator(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    /**
     * Switches to the authentication screen by asking the SceneManager to load the AUTH_FXML.
     */
    @Override
    public void goToAuth() {
        sceneManager.switchScene(UIConstants.AUTH_FXML);
    }

    /**
     * Switches to the lobby screen by asking the SceneManager to load the LOBBY_FXML.
     */
    @Override
    public void goToLobby() {
        sceneManager.switchScene(UIConstants.LOBBY_FXML);
    }

    /**
     * Switches to the game screen by asking the SceneManager to load the GAME_FXML.
     */
    @Override
    public void goToGame() {
        sceneManager.switchScene(UIConstants.GAME_FXML);
    }

    /**
     * Opens the game room creator window.
     *
     * @param primaryController the controller that requested this dialog
     * @return the newly opened Stage
     */
    @Override
    public Stage openGameRoomCreator(Object primaryController) {
        return sceneManager.openWindow(UIConstants.GAME_ROOM_FORM_FXML, "Create new room", primaryController);
    }

    /**
     * Opens the game room manager window.
     *
     * @param primaryController the controller that requested this dialog
     * @return the newly opened Stage
     */
    @Override
    public Stage openGameRoomManager(Object primaryController) {
        return sceneManager.openWindow(UIConstants.GAME_ROOM_MANAGER_FXML, "Manage my room", primaryController);
    }

    /**
     * Opens the game result window
     */
    @Override
    public void openGameResultWindow() {
        sceneManager.openWindow(UIConstants.GAME_RESULT_FXML, "Game result", null);
    }

    /**
     * Displays an error message as an Alert.
     *
     * @param message the error text to display
     */
    @Override
    public void showError(String message) {
        sceneManager.showErrorAlert(message);
    }

    /**
     * Displays a confirmation dialog with the specified message.
     *
     * @param message the confirmation text to display
     * @return true if the user confirmed; false otherwise
     */
    @Override
    public boolean showConfirmation(String message) {
        return sceneManager.showConfirmationAlert(message);
    }

    /**
     * Closes the currently opened window.
     */
    @Override
    public void closeWindow() {
        sceneManager.closeWindow();
    }

    /**
     * Closes the currently displayed confirmation dialog.
     */
    @Override
    public void closeConfirmationAlert() {
        sceneManager.closeConfirmationAlert();
    }

    /**
     * Returns the controller associated with the currently opened window.
     *
     * @return the primary controller
     */
    @Override
    public Object getPrimaryController() {
        return sceneManager.getPrimaryController();
    }
}