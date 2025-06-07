package cz.vse.pexeso.view.helper;

import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.navigation.UIConstants;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates the Lobby UI buttons (“Manage Room” and “Ready”) based on the current session state.
 * Handles enabling/disabling, setting text, and styling.
 *
 * @author kott10
 * @version June 2025
 */
public final class LobbyUIUpdater {
    private static final Logger log = LoggerFactory.getLogger(LobbyUIUpdater.class);

    private LobbyUIUpdater() {
    }

    /**
     * Updates the “Manage Room” button: shows “Create Room” if not in a room, “Manage Room” if hosting,
     * or disables it if in a room but not host.
     *
     * @param lobbyModel       Model providing session and room info
     * @param manageRoomButton Button to update
     */
    public static void updateManageRoomButton(LobbyModel lobbyModel, Button manageRoomButton) {
        String currentRoomId = lobbyModel.getCurrentGameRoomId();
        boolean isHost = lobbyModel.isHosting();

        if (currentRoomId == null) {
            editManageRoomButton(false, "Create new room", manageRoomButton);
        } else if (isHost) {
            editManageRoomButton(false, "Manage my room", manageRoomButton);
        } else {
            editManageRoomButton(true, "Create new room", manageRoomButton);
        }
        log.debug("Updated manageRoomButton: currentRoomId={}, isHost={}", currentRoomId, isHost);
    }

    /**
     * Updates the “Ready” button: disables it if not in a room, sets text and color if host.
     *
     * @param lobbyModel  Model providing session and room info
     * @param readyButton Button to update
     */
    public static void updateReadyButton(LobbyModel lobbyModel, Button readyButton) {
        String currentRoomId = lobbyModel.getCurrentGameRoomId();
        boolean isReady = lobbyModel.isReady();
        boolean isHost = lobbyModel.isHosting();

        if (currentRoomId == null) {
            editReadyButton(true, "Not ready", UIConstants.RED_COLOR, readyButton);
        } else if (!isReady) {
            editReadyButton(false, null, null, readyButton);
        } else if (isHost) {
            editReadyButton(true, "Ready", UIConstants.GREEN_COLOR, readyButton);
        }
        log.debug("Updated readyButton: currentRoomId={}, isReady={}, isHost={}",
                currentRoomId, isReady, isHost);
    }

    /**
     * Helper to set text and disable state of the “Manage Room” button on the JavaFX thread.
     */
    private static void editManageRoomButton(
            boolean disabled,
            String text,
            Button manageRoomButton
    ) {
        manageRoomButton.setDisable(disabled);
        if (text != null) {
            manageRoomButton.setText(text);
        }
    }

    /**
     * Helper to set text, style, and disable state of the “Ready” button on the JavaFX thread.
     *
     * @param disabled    true to disable the button
     * @param text        text to display
     * @param color       CSS color style
     * @param readyButton Button to update
     */
    public static void editReadyButton(
            boolean disabled,
            String text,
            String color,
            Button readyButton
    ) {
        readyButton.setDisable(disabled);
        if (text != null) {
            readyButton.setText(text);
        }
        if (color != null) {
            readyButton.setStyle(color);
        }
    }
}