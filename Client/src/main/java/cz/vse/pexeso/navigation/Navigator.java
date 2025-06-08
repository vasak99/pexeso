package cz.vse.pexeso.navigation;

import javafx.stage.Stage;

/**
 * Defines navigation operations for the UI.
 *
 * @author kott10
 * @version June 2025
 */
public interface Navigator {
    /**
     * Switches the application to the authentication screen.
     */
    void goToAuth();

    /**
     * Switches the application to the lobby screen.
     */
    void goToLobby();

    /**
     * Switches the application to the game screen.
     */
    void goToGame();

    /**
     * Opens the game room creator.
     *
     * @param primaryController the controller that requested this dialog
     * @return the newly opened Stage for the game room creation
     */
    Stage openGameRoomCreator(Object primaryController);

    /**
     * Opens the game room manager.
     *
     * @param primaryController the controller that requested this dialog
     * @return the newly opened Stage for managing the game room
     */
    Stage openGameRoomManager(Object primaryController);

    /**
     * Opens the game result window to show final scores.
     */
    void openGameResultWindow();

    /**
     * Displays an error message as an Alert.
     *
     * @param message the error text to display
     */
    void showError(String message);

    /**
     * Displays a confirmation dialog with the specified message.
     *
     * @param message the confirmation text to display
     * @return true if the user confirmed; false otherwise
     */
    boolean showConfirmation(String message);

    /**
     * Closes the currently opened window.
     */
    void closeWindow();

    /**
     * Closes the currently displayed confirmation.
     */
    void closeConfirmationAlert();

    /**
     * Returns the controller associated with the currently active window.
     *
     * @return the primary controller
     */
    Object getPrimaryController();
}
