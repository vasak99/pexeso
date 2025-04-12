package cz.vse.pexeso;

import cz.vse.pexeso.common.MyTestClass;
import cz.vse.pexeso.helper.SceneManager;
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
        SceneManager.switchScene("/cz/vse/pexeso/fxml/login.fxml");
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}