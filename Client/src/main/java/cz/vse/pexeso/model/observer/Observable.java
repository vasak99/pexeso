package cz.vse.pexeso.model.observer;

public interface Observable {
    void register(MessageTypeClient type, Observer observer);

    void registerWithData(MessageTypeClient type, ObserverWithData observer);

    void notifyObservers(MessageTypeClient type);

    void notifyObservers(MessageTypeClient type, Object data);
}
