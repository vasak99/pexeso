package cz.vse.pexeso.main;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.utils.Observable;
import cz.vse.pexeso.utils.Observer;

public class Connection implements Runnable, Observable {

    public static final Logger log = LoggerFactory.getLogger(Connection.class);

    private Socket socket;
    private Set<Observer> observers = new HashSet<>();

    public Connection(Socket socket) {
        this.socket = socket;
    }

    public void run() {
 
    }

    public void terminate() {
        try {
            this.socket.close();
            log.info("Connection terminated");
        } catch(IOException e) {
            log.error("IOException occurred when terminating socket: " + e);
        }
        this.notifyObservers(this);

    }

    @Override
    public void subscribe(Observer obs) {
        this.observers.add(obs);
    }

    public void unsubscribe(Observer obs) {
        this.observers.remove(obs);
    }

    public void notifyObservers(Observable obs) {
        for (Observer observer : this.observers) {
            observer.onNotify(obs);
        }
    }

}
