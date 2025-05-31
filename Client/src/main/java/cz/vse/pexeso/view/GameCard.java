package cz.vse.pexeso.view;

import cz.vse.pexeso.navigation.UIConstants;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

public class GameCard extends Button {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private final int row;
    private final int column;
    private String imageName;
    private Status status;
    public static final int size = 100;

    public GameCard(Status status, int row, int column) {
        setup();
        this.status = status;
        this.row = row;
        this.column = column;
        this.imageName = "";

        if (this.status == Status.HIDDEN) {
            hide();
        }
    }

    public GameCard(Status status, int row, int column, String imageName) {
        setup();
        this.status = status;
        this.row = row;
        this.column = column;
        this.imageName = imageName;

        reveal(this.imageName);
    }

    private void setup() {
        //setup dimensions, color, behaviour etc.
        setMinSize(size, size);
        setMaxSize(size, size);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Status getStatus() {
        return status;
    }

    public String getImageName() {
        return imageName;
    }

    public void reveal(String imageName) {
        flip(Status.REVEALED, imageName);
    }

    public void hide() {
        flip(Status.HIDDEN, UIConstants.DEFAULT_CARD_IMAGE);
    }

    private void flip(Status status, String imageName) {
        this.status = status;
        this.imageName = imageName;

        Image image = imageCache.computeIfAbsent(imageName, name -> new Image(name, size * 0.95, size * 0.95, true, true));
        ImageView imageView = new ImageView(image);

        Platform.runLater(() -> setGraphic(imageView));
    }

    public void markCompleted(String color) {
        this.status = Status.COMPLETED;
        setStyle(color);
    }

    public enum Status {
        NULL,
        HIDDEN,
        REVEALED,
        COMPLETED
    }
}
