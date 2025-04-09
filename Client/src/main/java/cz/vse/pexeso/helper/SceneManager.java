package cz.vse.pexeso.helper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Manages JavaFX scenes. Allows to switch between different scenes.
 */
public class SceneManager {
    public static final Logger log = LoggerFactory.getLogger(SceneManager.class);
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Switches the current scene to the specified FXML file.
     *
     * @param fxmlFile The path to the FXML file.
     */
    public static void switchScene(String fxmlFile) {
        try {
            log.debug("Switching scene to: {}", fxmlFile);
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlFile));
            primaryStage.setScene(new Scene(root));
        } catch (IOException e) {
            log.error("Error loading FXML file: {}", fxmlFile, e);
        }
    }


}
