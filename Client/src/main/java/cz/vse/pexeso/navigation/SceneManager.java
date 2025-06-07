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

/**
 * Manages scene switching and window/dialog operations for the application.
 * Uses FXMLLoader to load FXML files, sets the controller factory to use DI, and applies stylesheet.
 * Provides methods to show error and confirmation alerts consistently.
 *
 * @author kott10
 * @version June 2025
 */
public class SceneManager {
    private static final Logger log = LoggerFactory.getLogger(SceneManager.class);

    private final Injector injector;

    private Stage primaryStage;
    private Stage openedWindow;
    private Object primaryController;
    private Alert openedConfirmationAlert;

    /**
     * Constructs a SceneManager with the given Injector.
     *
     * @param injector the Injector providing dependencies for controllers
     */
    public SceneManager(Injector injector) {
        this.injector = injector;
        log.info("Creating SceneManager instance");
    }

    /**
     * Sets the primaryStage used for scene switching.
     *
     * @param stage the JavaFX primary stage
     */
    public void setStage(Stage stage) {
        this.primaryStage = stage;
        log.debug("Primary stage set in SceneManager");
    }

    /**
     * Replaces the current scene on the primaryStage with the FXML file specified.
     *
     * @param fxmlFile the path to the FXML file
     */
    public void switchScene(String fxmlFile) {
        if (primaryStage == null) {
            log.error("Primary stage is not initialized. Cannot switch scenes.");
            return;
        }
        try {
            log.debug("Switching scene to: {}", fxmlFile);
            primaryStage.setScene(load(fxmlFile));
            log.info("Scene switched to {}", fxmlFile);
        } catch (IOException e) {
            log.error("Error loading FXML file: {}", fxmlFile, e);
            showErrorAlert("Failed to load scene: " + fxmlFile);
        }
    }

    /**
     * Opens a new window with the specified FXML file and title.
     *
     * @param fxmlFile          the path to the FXML file
     * @param title             the window title
     * @param primaryController the controller to associate as the “parent”
     * @return the newly opened Stage
     */
    public Stage openWindow(String fxmlFile, String title, Object primaryController) {
        try {
            Stage stage = new Stage();
            stage.setScene(load(fxmlFile));
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            openedWindow = stage;
            this.primaryController = primaryController;
            log.info("Opened new window '{}' with FXML: {}", title, fxmlFile);
        } catch (IOException e) {
            log.error("Error loading FXML file for window: {}", fxmlFile, e);
            showErrorAlert("Failed to open window: " + fxmlFile);
        }
        return openedWindow;
    }

    /**
     * Loads a Scene from the given FXML file. Applies a stylesheet.
     *
     * @param fxmlFile the path to the FXML resource
     * @return a loaded Scene
     */
    private Scene load(String fxmlFile) throws IOException {
        URL fxmlUrl = SceneManager.class.getResource(fxmlFile);
        log.debug("FXML URL: {}", fxmlUrl);
        if (fxmlUrl == null) {
            throw new IOException("FXML file not found: " + fxmlFile);
        }
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        loader.setControllerFactory(this::createController);

        Scene scene = new Scene(loader.load());

        URL source = SceneManager.class.getResource(UIConstants.STYLE);
        if (source != null) {
            scene.getStylesheets().add(source.toExternalForm());
            log.debug("Applied stylesheet: {}", UIConstants.STYLE);
        }
        return scene;
    }

    /**
     * Closes the currently opened window (if any) and clears its controller reference.
     */
    public void closeWindow() {
        if (openedWindow != null) {
            openedWindow.close();
            log.info("Closed window: {}", openedWindow.getTitle());
            openedWindow = null;
        }
        primaryController = null;
        log.debug("Primary controller reference cleared");
    }

    /**
     * Displays a confirmation alert with the given text and waits for user input.
     *
     * @param text the message to display
     * @return true if the user clicked OK; false otherwise
     */
    public boolean showConfirmationAlert(String text) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(text);

        openedConfirmationAlert = confirmationAlert;
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        openedConfirmationAlert = null;

        boolean confirmed = result.isPresent() && result.get() == ButtonType.OK;
        log.debug("Confirmation result for '{}': {}", text, confirmed);
        return confirmed;
    }

    /**
     * Closes the currently displayed confirmation alert (if any).
     */
    public void closeConfirmationAlert() {
        if (openedConfirmationAlert != null) {
            openedConfirmationAlert.close();
            log.debug("Closed confirmation alert");
            openedConfirmationAlert = null;
        }
    }

    /**
     * Displays an error alert with the given text. Blocks until the user closes it.
     *
     * @param text the error message to display
     */
    public void showErrorAlert(String text) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(text);
        errorAlert.showAndWait();
        log.error("Displayed error alert: {}", text);
    }

    /**
     * Factory method for creating controllers via DI. Matches known controller classes to constructors
     * that accept Navigator, Model, and Injector as needed.
     *
     * @param controllerClass the Class object of the controller to create
     * @return an instance of the requested controller
     */
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
        log.warn("Unhandled controller class: {}", controllerClass.getName());
        return null;
    }

    /**
     * Returns the primary controller associated with the currently opened window.
     *
     * @return the primary controller object, or null if none is set
     */
    public Object getPrimaryController() {
        return primaryController;
    }
}