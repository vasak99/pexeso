package cz.vse.pexeso.view.helper;

import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common utility methods for UI operations: disabling controls and clearing text fields.
 *
 * @author kott10
 * @version June 2025
 */
public final class UICommonHelper {
    private static final Logger log = LoggerFactory.getLogger(UICommonHelper.class);

    private UICommonHelper() {
    }

    /**
     * Sets the disable state for all provided controls.
     *
     * @param disable  true to disable all, false to enable
     * @param controls Controls to disable/enable
     */
    public static void setDisableAll(boolean disable, Control... controls) {
        for (Control c : controls) {
            if (c != null) {
                c.setDisable(disable);
            }
        }
        log.debug("Set disable={} on {} controls", disable, controls.length);
    }

    /**
     * Clears the text in all provided TextFields.
     *
     * @param fields varargs of TextField to clear
     */
    public static void clearFields(TextField... fields) {
        for (TextField f : fields) {
            if (f != null) {
                f.clear();
            }
        }
        log.debug("Cleared {} text fields", fields.length);
    }
}