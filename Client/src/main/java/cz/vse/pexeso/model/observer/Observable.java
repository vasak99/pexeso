package cz.vse.pexeso.model.observer;

public interface Observable {

    void register(MessageType messageType, Observer observer);
}
