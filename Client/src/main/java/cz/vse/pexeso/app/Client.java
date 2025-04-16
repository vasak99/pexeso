package cz.vse.pexeso.app;

import cz.vse.pexeso.common.MyTestClass;
import cz.vse.pexeso.util.SceneManager;
import cz.vse.pexeso.util.UIConstants;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client extends Application {
    public static final Logger log = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        System.out.println(MyTestClass.getGreeting("client"));

        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting client application");
        SceneManager.setStage(primaryStage);
        SceneManager.switchScene(UIConstants.LOGIN_FXML);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}