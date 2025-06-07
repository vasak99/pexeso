package cz.vse.pexeso.app;

/**
 * Does not extend Application, workaround for creating fat-JAR
 */
public class Launcher {
    public static void main(String[] args) {
        javafx.application.Application.launch(Client.class, args);
    }
}
