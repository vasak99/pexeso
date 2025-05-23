package cz.vse.pexeso.model.service;

import cz.vse.pexeso.model.ClientSession;

public class SessionService {
    private ClientSession clientSession;

    public ClientSession getSession() {
        return clientSession;
    }

    public void setSession(ClientSession session) {
        this.clientSession = session;
    }
}
