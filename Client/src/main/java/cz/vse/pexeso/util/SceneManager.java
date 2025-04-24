package cz.vse.pexeso.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

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
            Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fxmlFile)));
            primaryStage.setScene(new Scene(root));
        } catch (IOException e) {
            log.error("Error loading FXML file: {}", fxmlFile, e);
        }
    }

    public static void openWindow(String fxmlFile, String title) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fxmlFile)));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            log.error("Error loading FXML file: {}", fxmlFile, e);
        }
    }
}
