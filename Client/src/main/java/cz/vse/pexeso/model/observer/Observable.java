package cz.vse.pexeso.model.observer;

import cz.vse.pexeso.common.message.MessageType;

/**
 * Observable interface, allows registration, unregistration and notification
 *
 * @author kott10
 * @version June 2025
 */
public interface Observable {
    /**
     * Registers a simple observer for the given MessageType.
     */
    void register(MessageType type, Observer observer);

    /**
     * Registers a data observer for the given MessageType.
     */
    void registerWithData(MessageType type, ObserverWithData observerWithData);

    /**
     * Unregisters a simple observer from the given MessageType.
     */
    void unregister(MessageType type, Observer observer);

    /**
     * Unregisters a data observer from the given MessageType.
     */
    void unregisterWithData(MessageType type, ObserverWithData observerWithData);

    /**
     * Notifies all registered simple observers of the given MessageType.
     */
    void notifyObservers(MessageType type);

    /**
     * Notifies all registered data-carrying observers of the given MessageType with the provided data.
     */
    void notifyObservers(MessageType type, Object data);
}
