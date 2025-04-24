package cz.vse.pexeso.game;

public class Card {

    private String image;
    private int id;

    public Card(int id) {
        this.id = id;
    }

    public String getImage() {
        return this.image;
    }

    public int getId() {
        return this.id;
    }

}
