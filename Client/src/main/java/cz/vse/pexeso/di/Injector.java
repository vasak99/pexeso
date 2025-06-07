package cz.vse.pexeso.di;

import cz.vse.pexeso.model.model.AuthModel;
import cz.vse.pexeso.model.model.GameModel;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.model.result.HandlerFactory;
import cz.vse.pexeso.model.service.*;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.navigation.SceneManager;
import cz.vse.pexeso.navigation.SceneNavigator;
import cz.vse.pexeso.network.ConnectionService;
import cz.vse.pexeso.network.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for creating and wiring dependencies
 *
 * @author kott10
 * @version June 2025
 */
public class Injector {
    private static final Logger log = LoggerFactory.getLogger(Injector.class);

    private final SceneManager sceneManager;
    private final Navigator navigator;

    private final ConnectionService connectionService;
    private final RedirectService redirectService;
    private final SessionService sessionService;

    private final AuthService authService;
    private final LobbyService lobbyService;
    private final GameRoomService gameRoomService;
    private final GameService gameService;

    private final AuthModel authModel;
    private final LobbyModel lobbyModel;
    private final GameRoomModel gameRoomModel;
    private final GameModel gameModel;

    private final HandlerFactory handlerFactory;

    /**
     * Constructs a new Injector, initializes all services, models, handler factories, and navigation.
     */
    public Injector() {
        log.info("Initializing Injector and all components");

        this.connectionService = new ConnectionService();
        this.redirectService = new RedirectService(connectionService);
        this.sessionService = new SessionService();

        this.authService = new AuthService(connectionService);
        this.lobbyService = new LobbyService(connectionService);
        this.gameRoomService = new GameRoomService(connectionService);
        this.gameService = new GameService(connectionService);

        this.handlerFactory = new HandlerFactory(connectionService);

        this.authModel = new AuthModel(authService, sessionService, redirectService);
        this.lobbyModel = new LobbyModel(lobbyService, sessionService, redirectService);
        this.gameRoomModel = new GameRoomModel(gameRoomService, sessionService, redirectService);
        this.gameModel = new GameModel(gameService, sessionService, redirectService);

        this.sceneManager = new SceneManager(this);
        this.navigator = new SceneNavigator(sceneManager);

        log.info("Injector initialization complete");
    }

    public ConnectionService getConnectionService() {
        return connectionService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public AuthModel getAuthModel() {
        return authModel;
    }

    public LobbyModel getLobbyModel() {
        return lobbyModel;
    }

    public GameRoomModel getGameRoomModel() {
        return gameRoomModel;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public HandlerFactory getHandlerFactory() {
        return handlerFactory;
    }

    /**
     * Closes connection
     */
    public void shutdown() {
        log.info("Shutting down network services");
        if (connectionService.getConnection() != null) {
            try {
                connectionService.getConnection().close();
                log.info("Connection closed successfully");
            } catch (Exception e) {
                log.error("Error while closing connection: ", e);
            }
        } else {
            log.warn("No active connection to close");
        }
    }
}