package cz.vse.pexeso.util;

import cz.vse.pexeso.common.environment.Variables;
import cz.vse.pexeso.model.GameRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input forms such as login, registration, and game room creation.
 * Provides methods that return an error String if validation fails or null on success.
 *
 * @author kott10
 * @version June 2025
 */
public final class FormValidator {
    private static final Logger log = LoggerFactory.getLogger(FormValidator.class);
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    private FormValidator() {
    }

    /**
     * Validates the login form fields.
     *
     * @param username the entered username
     * @param password the entered password
     * @return an error message if validation fails, or null if inputs are valid
     */
    public static String validateLoginForm(String username, String password) {
        log.debug("Validating login form for username: {}", username);
        String warning = validateCommonFields(username, password);
        if (warning != null) {
            log.warn("Login form validation failed: {}", warning);
        }
        return warning;
    }

    /**
     * Validates the registration form fields, including confirm password.
     *
     * @param username        the entered username
     * @param password        the entered password
     * @param confirmPassword the confirmation password
     * @return an error message if validation fails, or null if inputs are valid
     */
    public static String validateRegisterForm(String username, String password, String confirmPassword) {
        log.debug("Validating registration form for username: {}", username);
        String warning = validateCommonFields(username, password);
        if (warning != null) {
            log.warn("Registration form validation failed (common fields): {}", warning);
            return warning;
        }

        if (confirmPassword.trim().isEmpty()) {
            log.warn("Registration form validation failed: confirm password is empty");
            return "Please fill in all fields.";
        }

        if (isTooLong(username)) {
            log.warn("Registration form validation failed: username too long");
            return "Name can not be longer than 16 characters";
        }

        if (passwordsDoNotMatch(password, confirmPassword)) {
            log.warn("Registration form validation failed: passwords do not match");
            return "Passwords do not match.";
        }

        if (weakPassword(password)) {
            log.warn("Registration form validation failed: weak password");
            return "Password must be at least 8 characters long.";
        }

        return null;
    }

    /**
     * Validates the game room creation/edit form.
     *
     * @param name            the desired room name
     * @param boardSize       the selected board size
     * @param customBoardSize the custom size string if boardSize == CUSTOM
     * @return an error message if validation fails, or null if inputs are valid
     */
    public static String validateGameRoomForm(String name, GameRoom.BoardSize boardSize, String customBoardSize) {
        log.debug("Validating game room form for name: {}", name);
        if (name.trim().isEmpty()) {
            log.warn("GameRoom form validation failed: name is empty");
            return "Choose name";
        }

        if (isTooLong(name)) {
            log.warn("GameRoom form validation failed: name too long");
            return "Name can not be longer than 16 characters";
        }

        if (isNameNotSafe(name)) {
            log.warn("GameRoom form validation failed: invalid characters in name");
            return "Name contains invalid characters.";
        }

        if (boardSize == null) {
            log.warn("GameRoom form validation failed: boardSize is null");
            return "Choose board size";
        }

        if (boardSize == GameRoom.BoardSize.CUSTOM) {
            if (customBoardSize.trim().isEmpty()) {
                log.warn("GameRoom form validation failed: custom board size is required but empty");
                return "Choose custom board size";
            }
            int customSize;
            try {
                customSize = Integer.parseInt(customBoardSize);
            } catch (NumberFormatException e) {
                log.warn("GameRoom form validation failed: custom board size parse error", e);
                return "Invalid custom board size";
            }
            if (customSize < Variables.MIN_CARDS || customSize > Variables.MAX_CARDS || customSize % 2 != 0) {
                log.warn("GameRoom form validation failed: custom board size out of bounds");
                return String.format("The board size must be an even number between %d and %d.", Variables.MIN_CARDS, Variables.MAX_CARDS);
            }
        }
        return null;
    }

    /**
     * Validates that username and password are non-empty and safe.
     */
    private static String validateCommonFields(String username, String password) {
        if (isEmpty(username, password)) {
            log.warn("Form validation failed: some required fields are empty");
            return "Please fill in all fields.";
        }

        if (isNameNotSafe(username)) {
            log.warn("Form validation failed: username contains invalid characters");
            return "Name contains invalid characters.";
        }

        if (containsLineBreaks(password)) {
            log.warn("Form validation failed: password contains line breaks");
            return "Password contains invalid characters.";
        }
        return null;
    }

    /**
     * Returns true if any of the provided strings is null or empty.
     */
    private static boolean isEmpty(String... textFields) {
        for (String textField : textFields) {
            if (textField == null || textField.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if passwords do not match.
     */
    private static boolean passwordsDoNotMatch(String password, String confirmPassword) {
        return !password.equals(confirmPassword);
    }

    /**
     * Returns true if password length is less than 8.
     */
    private static boolean weakPassword(String password) {
        return password.length() < 8;
    }

    /**
     * Returns true if name length exceeds 16 characters.
     */
    private static boolean isTooLong(String name) {
        return name.length() > 16;
    }

    /**
     * Returns true if username contains disallowed characters or line breaks.
     */
    private static boolean isNameNotSafe(String username) {
        return !USERNAME_PATTERN.matcher(username).matches() || containsLineBreaks(username);
    }

    /**
     * Returns true if the input contains newline or carriage return.
     */
    private static boolean containsLineBreaks(String input) {
        return input.contains("\n") || input.contains("\r");
    }
}