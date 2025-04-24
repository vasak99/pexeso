package cz.vse.pexeso.model;

import cz.vse.pexeso.service.AppServices;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;

public class ClientSession {
    private final String playerId;
    private final UserCredentials userCredentials;

    public ClientSession(String playerId, UserCredentials userCredentials) {
        this.playerId = playerId;
        this.userCredentials = userCredentials;
    }

    public String getPlayerId() {
        return playerId;
    }

    public UserCredentials getUser() {
        return userCredentials;
    }

    public void logout() {
        AppServices.clear();

        SceneManager.switchScene(UIConstants.LOGIN_FXML);
    }
}
