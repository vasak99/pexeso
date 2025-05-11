package cz.vse.pexeso.model.observer;

public interface Observable {
    void register(MessageTypeClient type, Observer observer);

    void registerWithData(MessageTypeClient type, ObserverWithData observerWithData);

    void unregister(MessageTypeClient type, Observer observer);

    void unregisterWithData(MessageTypeClient type, ObserverWithData observerWithData);

    void notifyObservers(MessageTypeClient type);

    void notifyObservers(MessageTypeClient type, Object data);
}
