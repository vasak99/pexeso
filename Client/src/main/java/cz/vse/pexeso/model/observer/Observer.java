package cz.vse.pexeso.model.observer;

/**
 * Simple observer - notified when a message without data is received.
 * The update method will be called by the Observable when a matching MessageType comes in.
 *
 * @author kott10
 * @version June 2025
 */
public interface Observer {

    /**
     * Called when the Observable dispatches a message of the registered type.
     */
    void update();
}
