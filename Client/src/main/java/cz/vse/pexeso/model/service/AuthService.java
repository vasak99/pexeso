package cz.vse.pexeso.model.service;

import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.util.MessageBuilder;

public class AuthService {
    private final ConnectionService connectionService;

    public AuthService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public void sendLoginRequest(UserCredentials userCredentials) {
        String message = MessageBuilder.buildLoginMessage(userCredentials);
        connectionService.send(message);
    }

    public void sendRegisterRequest(UserCredentials userCredentials) {
        String message = MessageBuilder.buildRegisterMessage(userCredentials);
        connectionService.send(message);
    }
}
