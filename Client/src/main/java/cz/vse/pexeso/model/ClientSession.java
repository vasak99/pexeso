package cz.vse.pexeso.model;

import cz.vse.pexeso.service.AppServices;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;

public class ClientSession {
    private final String playerId;
    private final User user;

    public ClientSession(String playerId, User user) {
        this.playerId = playerId;
        this.user = user;
    }

    public String getPlayerId() {
        return playerId;
    }

    public User getUser() {
        return user;
    }

    public void logout() {
        AppServices.clear();

        SceneManager.switchScene(UIConstants.LOGIN_FXML);
    }
}
