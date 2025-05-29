package cz.vse.pexeso.navigation;

import cz.vse.pexeso.controller.*;
import cz.vse.pexeso.di.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class SceneManager {
    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);

    private final Injector injector;

    private Stage primaryStage;
    private Stage openedWindow;
    private Alert openedConfirmationAlert;

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

            primaryStage.setScene(load(fxmlFile));
        } catch (IOException e) {
            log.error("Error loading FXML file: {}", fxmlFile, e);
        }
    }

    public Stage openWindow(String fxmlFile, String title) {
        try {
            Stage stage = new Stage();
            stage.setScene(load(fxmlFile));

            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            openedWindow = stage;
        } catch (IOException e) {
            log.error("Error loading FXML file: {}", fxmlFile, e);
        }
        return openedWindow;
    }

    private Scene load(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        loader.setControllerFactory(this::createController);

        Scene scene = new Scene(loader.load());

        URL source = getClass().getResource("/cz/vse/pexeso/style.css");
        if (source != null) {
            scene.getStylesheets().add(source.toExternalForm());
        }
        return scene;
    }

    private Object createController(Class<?> controllerClass) {
        if (controllerClass == AuthController.class) {
            return new AuthController(injector.getNavigator(), injector.getAuthModel(), injector);
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
        if (controllerClass == GameController.class) {
            return new GameController(injector.getNavigator(), injector.getGameModel(), injector);
        }
        if (controllerClass == GameResultController.class) {
            return new GameResultController(injector.getNavigator(), injector.getGameModel());
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
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(text);
        errorAlert.showAndWait();
    }

    public boolean showConfirmationAlert(String text) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(text);

        openedConfirmationAlert = confirmationAlert;
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        openedConfirmationAlert = null;
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void closeConfirmationAlert() {
        if (openedConfirmationAlert != null) {
            openedConfirmationAlert.close();
            openedConfirmationAlert = null;
        }
    }
}