package cz.vse.pexeso.util;

public final class Strings {
    private Strings() {
    }

    public static final String GAME_TITLE = "Pexeso Game";
    public static final String LOG_IN = "Log in";
    public static final String REGISTER = "Register";
    public static final String REGISTER_LINK = "Don't have an account yet? Register here!";
    public static final String LOGIN_LINK = "Already have an account? Log in here!";

    public static final String FILL_IN_FIELDS = "Please fill in all fields.";
    public static final String NAME_TOO_LONG = "Name can not be longer than 16 characters";
    public static final String PASSWORD_NO_MATCH = "Passwords do not match.";
    public static final String PASSWORD_SHORT = "Password must be at least 8 characters long.";
    public static final String CHOOSE_NAME = "Choose name";
    public static final String CHOOSE_BOARD_SIZE = "Choose board size";
    public static final String CHOOSE_CUSTOM_BOARD_SIZE = "Choose custom board size";
    public static final String BOARD_SIZE_REQUIREMENTS = "The board size must be an even number between %d and %d.";
    public static final String TRY_AGAIN = "%s, please try again.";

    public static final String AVAILABLE_ROOMS = "Available rooms for %s";
    public static final String CREATE_ROOM = "Create new room";
    public static final String MANAGE_ROOM = "Manage my room";

    public static final String NOT_READY = "Not ready";
    public static final String READY = "Ready";
    public static final String WAITING_FOR_PLAYERS = "Waiting for players";
    public static final String IN_PROGRESS = "In progress";

    public static final String JOIN = "Join";
    public static final String LEAVE = "Leave";
    public static final String KICK = "Kick";

    public static final String ERR = "Error";
    public static final String CONF = "Confirmation";

    public static final String KICK_ALERT = "You got kicked from the game room";

    private static final String SURE = "Are you sure you want to %s ?";
    public static final String GIVE_UP_CONFIRMATION = String.format(SURE, "give up");
    public static final String DELETE_ROOM_CONFIRMATION = String.format(SURE, "delete this game room");
    public static final String KICK_PLAYER_CONFIRMATION = String.format(SURE, "kick %s");
    public static final String LEAVE_ROOM_CONFIRMATION = String.format(SURE, "leave this game room");

    public static final String NO_PLAYERS = "No other players in this game room";
    public static final String NO_ROOMS = "No game room has been created yet";

    public static final String YOUR_TURN = "Your turn";
    public static final String OPP_TURN = "Opponent's turn";

    public static final String RESULT = "Game result";
}
