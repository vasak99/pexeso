package cz.vse.pexeso.game;

public class Card {

    private int id;
    private String image;
    private boolean isRevealed = false;

    public Card(int id, String image) {
        this.image = image;
        this.id = id;
    }

    public String getImage() {
        return this.image;
    }

    public int getId() {
        return this.id;
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
