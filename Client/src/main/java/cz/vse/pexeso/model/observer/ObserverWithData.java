package cz.vse.pexeso.model.observer;

/**
 * Data observer - notified when a message with data is received.
 * The update method will be called by the Observable when a matching MessageType comes in.
 *
 * @author kott10
 * @version June 2025
 */
public interface ObserverWithData {

    /**
     * Called when the Observable dispatches a message of the registered type.
     *
     * @param data data associated with the message
     */
    void update(Object data);
}
