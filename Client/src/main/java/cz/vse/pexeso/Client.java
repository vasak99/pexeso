package cz.vse.pexeso;

import cz.vse.pexeso.common.MyTestClass;
import cz.vse.pexeso.helper.SceneManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    public void start(Stage primaryStage) throws Exception {
        log.info("Starting client application");
        SceneManager.setStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("/cz/vse/pexeso/fxml/login.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}