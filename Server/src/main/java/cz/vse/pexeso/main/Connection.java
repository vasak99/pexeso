package cz.vse.pexeso.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.common.message.Message;
import cz.vse.pexeso.common.utils.StreamReader;
import cz.vse.pexeso.utils.Observable;
import cz.vse.pexeso.utils.Observer;

public class Connection implements Runnable, Observable {

    public static final Logger log = LoggerFactory.getLogger(Connection.class);

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private Set<Observer> observers = new HashSet<>();
    private boolean keepAlive = true;

    public Connection(Socket socket) {
        this.socket = socket;

        try {
            this.oos = new ObjectOutputStream(this.socket.getOutputStream());
            this.oos.flush();
            this.ois = new ObjectInputStream(this.socket.getInputStream());
        } catch(IOException e) {
            log.error("Failed to extract input/output streams: " + e);
            log.error("Attempting to close connection");

            this.terminate();
        }
    }

    @Override
    public void run() {
        while(keepAlive && !socket.isClosed()) {
            String msg = "";
            try {
                msg = StreamReader.readPacket(ois);
            } catch (Exception e) {
                log.error("An error occured while reading a packet: " + e);
            }

            Message message = new Message(msg);

            this.notifyObservers(this, message);
        }
    }

    public void terminate() {
        this.keepAlive = false;
        try {
            this.socket.close();
            log.info("Connection terminated");
        } catch(IOException e) {
            log.error("IOException occurred when terminating socket: " + e);
        }
    }

    @Override
    public void subscribe(Observer obs) {
        this.observers.add(obs);
    }

    public void unsubscribe(Observer obs) {
        this.observers.remove(obs);
    }

    public void notifyObservers(Observable obs, Object o) {
        for (Observer observer : this.observers) {
            observer.onNotify(obs, o);
        }
    }

    public void sendMessage(String msg) {
        try {
            StreamReader.writePacket(oos, msg);
        } catch (Exception e) {
            log.error("" + e);
        }
    }

}
