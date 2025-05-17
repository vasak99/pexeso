package cz.vse.pexeso.app;

import cz.vse.pexeso.di.Injector;
import cz.vse.pexeso.navigation.SceneManager;
import cz.vse.pexeso.navigation.UIConstants;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends Application {
    public static final Logger log = LoggerFactory.getLogger(Client.class);
    private Injector injector;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting client application");

        injector = new Injector();
        SceneManager sceneManager = injector.getSceneManager();
        sceneManager.setStage(primaryStage);

        sceneManager.switchScene(UIConstants.AUTH_FXML);
        primaryStage.setTitle("Pexeso");
        primaryStage.show();
    }

    @Override
    public void stop() {
        injector.shutdown();
    }
}