package cz.vse.pexeso.navigation;

import cz.vse.pexeso.controller.*;
import cz.vse.pexeso.di.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class SceneManager {
    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);

    private final Injector injector;

    private Stage primaryStage;
    private Stage openedWindow;

    public SceneManager(Injector injector) {
        this.injector = injector;
        log.info("Creating SceneManager instance");
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Switches the current scene to the specified FXML file.
     *
     * @param fxmlFile The path to the FXML file.
     */
    public void switchScene(String fxmlFile) {
        try {
            log.debug("Switching scene to: {}", fxmlFile);

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            loader.setControllerFactory(this::createController);
            Parent root = loader.load();

            primaryStage.setScene(new Scene(root));
        } catch (IOException e) {
            log.error("Error loading FXML file: {}", fxmlFile, e);
        }
    }

    public Stage openWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            loader.setControllerFactory(this::createController);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setTitle(title);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            openedWindow = stage;
        } catch (IOException e) {
            log.error("Error loading FXML file: {}", fxmlFile, e);
        }
        return openedWindow;
    }

    private Object createController(Class<?> controllerClass) {
        if (controllerClass == LoginController.class) {
            return new LoginController(injector.getNavigator(), injector.getAuthModel(), injector);
        }
        if (controllerClass == RegisterController.class) {
            return new RegisterController(injector.getNavigator(), injector.getAuthModel(), injector);
        }
        if (controllerClass == LobbyController.class) {
            return new LobbyController(injector.getNavigator(), injector.getLobbyModel(), injector);
        }
        if (controllerClass == GameRoomCreationFormController.class) {
            return new GameRoomCreationFormController(injector.getNavigator(), injector.getGameRoomModel(), injector);
        }
        if (controllerClass == GameRoomManagerController.class) {
            return new GameRoomManagerController(injector.getNavigator(), injector.getGameRoomModel(), injector);
        }
        return null;
    }


    public void closeWindow() {
        if (openedWindow != null) {
            openedWindow.close();
            openedWindow = null;
        }
    }

    public void showErrorAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    public boolean showConfirmationAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(text);

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }
}