package cz.vse.pexeso.di;

import cz.vse.pexeso.model.model.AuthModel;
import cz.vse.pexeso.model.model.GameRoomModel;
import cz.vse.pexeso.model.model.LobbyModel;
import cz.vse.pexeso.model.result.*;
import cz.vse.pexeso.model.service.*;
import cz.vse.pexeso.navigation.Navigator;
import cz.vse.pexeso.navigation.SceneManager;
import cz.vse.pexeso.navigation.SceneNavigator;
import cz.vse.pexeso.network.RedirectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final AuthModel authModel;
    private final LobbyModel lobbyModel;
    private final GameRoomModel gameRoomModel;

    public Injector() {
        this.sceneManager = new SceneManager(this);
        this.navigator = new SceneNavigator(sceneManager);

        this.connectionService = new ConnectionService();
        this.redirectService = new RedirectService(connectionService);
        this.sessionService = new SessionService();

        this.authService = new AuthService(connectionService);
        this.lobbyService = new LobbyService(connectionService);
        this.gameRoomService = new GameRoomService(connectionService);


        this.authModel = new AuthModel(authService, sessionService);
        this.lobbyModel = new LobbyModel(lobbyService, sessionService);
        this.gameRoomModel = new GameRoomModel(gameRoomService, sessionService, redirectService);
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

    public AuthResultHandler createAuthResultHandler(AuthResultListener listener) {
        return new AuthResultHandler(listener, connectionService);
    }

    public LobbyResultHandler createLobbyResultHandler(LobbyResultListener listener) {
        return new LobbyResultHandler(listener, connectionService);
    }

    public GameRoomResultHandler createGameRoomResultHandler(GameRoomResultListener listener) {
        return new GameRoomResultHandler(listener, connectionService);
    }

    public void shutdown() {
        log.info("Shutting down network services");
        if (connectionService.getConnection() != null) {
            connectionService.getConnection().close();
        }
    }
}
