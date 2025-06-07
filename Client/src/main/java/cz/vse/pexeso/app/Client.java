package cz.vse.pexeso.app;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.navigation.SceneManager;
import cz.vse.pexeso.navigation.UIConstants;
import cz.vse.pexeso.util.MessageBuilder;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for the application.
 * Initializes dependency injection, configures the primary stage, and shows the authentication screen.
 * Handles application shutdown.
 *
 * @author kott10
 * @version June 2025
 */
public class Client extends Application {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private Injector injector;

    /**
     * Main method to launch the JavaFX application.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Called when the application is ready to start. Initializes DI and shows the auth scene.
     *
     * @param primaryStage the primary stage provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        log.info("Starting client application");
        if (primaryStage == null) {
            log.error("Primary stage is null in Application.start()");
            return;
        }

        injector = new Injector();
        log.debug("Injector initialized: {}", injector);

        SceneManager sceneManager = injector.getSceneManager();

        sceneManager.setStage(primaryStage);
        log.debug("Primary stage set in SceneManager");

        sceneManager.switchScene(UIConstants.AUTH_FXML);
        primaryStage.setTitle("Pexeso Game");
        primaryStage.show();
        log.info("Primary stage shown");
    }

    /**
     * Called when the JavaFX application is stopping. Sends logout message, closes connection.
     */
    @Override
    public void stop() {
        log.info("Stopping client application");
        try {
            if (injector != null) {
                sendLeaveMessage();
                sendLogoutMessage();
                injector.shutdown();
                log.info("Injector shutdown completed");
            } else {
                log.warn("Injector was not initialized. Skipping shutdown.");
            }
        } catch (Exception e) {
            log.error("Failed during shutdown: ", e);
        }
    }

    private void sendLeaveMessage() {
        if (injector == null) {
            return;
        }

        ClientSession clientSession = injector.getSessionService().getSession();

        if (clientSession.getCurrentGameRoom() == null) {
            return;
        }

        Long playerId = clientSession.getPlayerId();
        String gameId = clientSession.getCurrentGameRoom().getGameId();

        if (clientSession.getCurrentGameRoom().isInProgress()) {
            injector.getConnectionService().send(MessageBuilder.buildGiveUpMessage(gameId, playerId));
        } else {
            injector.getConnectionService().send(MessageBuilder.buildLeaveGameMessage(gameId, playerId));
        }
    }

    private void sendLogoutMessage() {
        if (injector == null) {
            return;
        }

        ClientSession clientSession = injector.getSessionService().getSession();

        Long playerId = clientSession.getPlayerId();
        String gameId = null;
        if (clientSession.getCurrentGameRoom() != null) {
            gameId = clientSession.getCurrentGameRoom().getGameId();
        }
        log.debug("Sending logout message.");
        injector.getConnectionService().send(MessageBuilder.buildLogoutMessage(playerId, gameId));
    }
}