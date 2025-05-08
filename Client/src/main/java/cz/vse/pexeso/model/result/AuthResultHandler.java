package cz.vse.pexeso.model.result;

import cz.vse.pexeso.model.observer.MessageTypeClient;
import cz.vse.pexeso.model.observer.ObserverWithData;
import cz.vse.pexeso.model.service.ConnectionService;

public class AuthResultHandler {
    private final ConnectionService connectionService;
    private AuthResultListener listener;

    private final ObserverWithData successObserver = data -> listener.onAuthSuccess((Long) data);
    private final ObserverWithData errorObserver = data -> listener.onAuthError((String) data);

    public AuthResultHandler(AuthResultListener listener, ConnectionService connectionService) {
        this.listener = listener;
        this.connectionService = connectionService;
    }

    public void register() {
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.AUTH_SUCCESS, successObserver);
        connectionService.getMessageHandler().registerWithData(MessageTypeClient.ERROR, errorObserver);
    }

    public void unregister() {
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.AUTH_SUCCESS, successObserver);
        connectionService.getMessageHandler().unregisterWithData(MessageTypeClient.ERROR, errorObserver);
    }
}
