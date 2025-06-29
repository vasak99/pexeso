package cz.vse.pexeso.navigation;

import java.util.List;

/**
 * Holds UI-related constants such as stylesheet paths, FXML file locations, default image,
 * and predefined color styles for various UI components. Provides a method to retrieve a list
 * of default player colors.
 *
 * @author kott10
 * @version June 2025
 */
public final class UIConstants {

    //CCS Stylesheet path
    public static final String STYLE = "/cz/vse/pexeso/style.css";

    //Screen and window fxml paths
    public static final String AUTH_FXML = "/cz/vse/pexeso/fxml/auth/auth.fxml";
    public static final String LOBBY_FXML = "/cz/vse/pexeso/fxml/lobby/lobby.fxml";
    public static final String GAME_ROOM_FORM_FXML = "/cz/vse/pexeso/fxml/lobby/gameRoomCreationForm.fxml";
    public static final String GAME_ROOM_MANAGER_FXML = "/cz/vse/pexeso/fxml/lobby/gameRoomManager.fxml";
    public static final String GAME_FXML = "/cz/vse/pexeso/fxml/game/game.fxml";
    public static final String GAME_RESULT_FXML = "/cz/vse/pexeso/fxml/game/result.fxml";

    //Default card image path
    public static final String DEFAULT_CARD_IMAGE = "/cz/vse/pexeso/fxml/game/defaultimage.png";

    //Player and Lobby color css
    public static final String RED_COLOR = "-fx-background-color: #ffb3ba;";
    public static final String ORANGE_COLOR = "-fx-background-color: #ffdfba;";
    public static final String YELLOW_COLOR = "-fx-background-color: #ffffba;";
    public static final String GREEN_COLOR = "-fx-background-color: #baffc9;";
    public static final String BLUE_COLOR = "-fx-background-color: #bae1ff;";
    public static final String GRAY_COLOR = "-fx-background-color: #e6e6e6;";

    //Result colors css
    public static final String GOLD_COLOR = "-fx-background-color: #ffd700;";
    public static final String SILVER_COLOR = "-fx-background-color: #c0c0c0;";
    public static final String BRONZE_COLOR = "-fx-background-color: #cd7f32;";

    /**
     * Returns an immutable list of default colors for players.
     */
    public static List<String> getColors() {
        return DEFAULT_PLAYER_COLORS;
    }

    private static final List<String> DEFAULT_PLAYER_COLORS = List.of(
            BLUE_COLOR,
            ORANGE_COLOR,
            RED_COLOR,
            GREEN_COLOR,
            YELLOW_COLOR
    );

    private UIConstants() {
    }
}
