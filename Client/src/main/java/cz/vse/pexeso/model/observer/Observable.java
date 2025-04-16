package cz.vse.pexeso.model.observer;

public interface Observable {

    void register(MessageTypeClient messageTypeClient, Observer observer);
}
