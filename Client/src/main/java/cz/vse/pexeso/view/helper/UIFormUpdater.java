package cz.vse.pexeso.view.helper;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Updates form labels to display error messages in red text.
 *
 * @author kott10
 * @version June 2025
 */
public final class UIFormUpdater {
    private static final Logger log = LoggerFactory.getLogger(UIFormUpdater.class);

    private UIFormUpdater() {
    }

    /**
     * Displays the given error message in the warning label with red text color.
     *
     * @param message      Error message to display
     * @param warningLabel Label to update
     */
    public static void showError(String message, Label warningLabel) {
        warningLabel.setText(message);
        warningLabel.setTextFill(Color.RED);
        log.debug("Displayed error message: {}", message);
    }
}