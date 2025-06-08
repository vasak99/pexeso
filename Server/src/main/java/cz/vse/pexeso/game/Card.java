package cz.vse.pexeso.game;

/**
 * Card object, contains identifier & image url
 */
public class Card {

    private int id;
    private String image;
    private boolean isRevealed = false;

    public Card(int id, String image) {
        this.id = id;
        this.image = image;
        this.id = id;
    }

    /**
     * Returns image path
     * @return String
     */
    public String getImage() {
        return this.image;
    }

    /**
     * Returns card identifier
     * @return int
     */
    public int getId() {
        return this.id;
    }

    /**
     * Is card revealed on the game board
     * @return boolean
     */
    public boolean getRevealed() {
        return this.isRevealed;
    }

    /**
     * Makes the card image visible on the game board
     */
    public void reveal() {
        this.isRevealed = true;
    }

    /**
     * Makes the card image hidden on the game board
     */
    public void hide() {
        this.isRevealed = false;
    }

}
