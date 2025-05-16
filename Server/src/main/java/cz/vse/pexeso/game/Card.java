package cz.vse.pexeso.game;

public class Card {

    private String image;
    private boolean isRevealed = false;

    public Card(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    public boolean getRevealed() {
        return this.isRevealed;
    }

    public void reveal() {
        this.isRevealed = true;
    }

    public void hide() {
        this.isRevealed = false;
    }

}
