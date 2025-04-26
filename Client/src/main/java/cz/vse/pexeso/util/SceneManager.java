package cz.vse.pexeso.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    private static Stage openedWindow;

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

    public static <T> T openWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(SceneManager.class.getResource(fxmlFile)));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            openedWindow = stage;

            return loader.getController();
        } catch (IOException e) {
            log.error("Error loading FXML file: {}", fxmlFile, e);
            return null;
        }
    }

    public static void closeWindow() {
        openedWindow.close();
        openedWindow = null;
    }

    public static void showErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Error");
        alert.setHeaderText(text);
        alert.showAndWait();
    }
}
