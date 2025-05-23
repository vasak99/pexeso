package cz.vse.pexeso.model.model;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.model.service.AuthService;
import cz.vse.pexeso.model.service.SessionService;
import cz.vse.pexeso.util.Updater;

public class AuthModel {
    private UserCredentials credentials;
    private final AuthService authService;
    private final SessionService sessionService;

    public AuthModel(AuthService authService, SessionService sessionService) {
        this.authService = authService;
        this.sessionService = sessionService;
    }

    public void attemptLogin() {
        authService.sendLoginRequest(credentials);
    }

    public void attemptRegister() {
        authService.sendRegisterRequest(credentials);
    }

    public void finalizeAuth(long playerId, String username, String password) {
        sessionService.setSession(new ClientSession(playerId, new UserCredentials(username, password)));
    }

    public void setCredentials(UserCredentials credentials) {
        this.credentials = credentials;
    }

    public void updateLobby(String data) {
        Updater.updateLobby(new GameListPayload(data));
    }
}
