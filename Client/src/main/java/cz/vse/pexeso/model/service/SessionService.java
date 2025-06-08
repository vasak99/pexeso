package cz.vse.pexeso.model.service;

import cz.vse.pexeso.model.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the current client session, allowing retrieval and replacement of the current ClientSession.
 *
 * @author kott10
 * @version June 2025
 */
public class SessionService {
    private static final Logger log = LoggerFactory.getLogger(SessionService.class);

    private ClientSession clientSession;

    /**
     * Retrieves the active client session.
     *
     * @return the current ClientSession
     */
    public ClientSession getSession() {
        return clientSession;
    }

    /**
     * Sets or replaces the client session.
     *
     * @param session the ClientSession to set
     */
    public void setSession(ClientSession session) {
        log.info("SessionService: setting new client session for playerId={}", session.getPlayerId());
        this.clientSession = session;
    }
}
