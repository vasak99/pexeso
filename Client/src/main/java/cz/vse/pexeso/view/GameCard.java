package cz.vse.pexeso.view;

import cz.vse.pexeso.navigation.UIConstants;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GameCard extends Button {
    private final int row;
    private final int column;
    private String imageUrl;
    private boolean isRevealed;
    private boolean isCompleted = false;

    public GameCard(int row, int column, String imageUrl) {
        //setup dimensions, color, behaviour etc.


        this.row = row;
        this.column = column;
        this.imageUrl = imageUrl;
        if (imageUrl == null) {
            hide();
        } else {
            reveal(imageUrl);
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void reveal(String imageUrl) {
        isRevealed = true;
        setGraphic(new ImageView(imageUrl));
    }

    public void hide() {
        isRevealed = false;
        Image defaultImage = new Image(UIConstants.DEFAULT_CARD_IMAGE);
        ImageView defaultImageView = new ImageView(defaultImage);
        defaultImageView.setFitWidth(75);
        defaultImageView.setPreserveRatio(true);
        setGraphic(defaultImageView);
    }

    public void markCompleted() {
        isCompleted = true;
        //mark player's color
    }
}
