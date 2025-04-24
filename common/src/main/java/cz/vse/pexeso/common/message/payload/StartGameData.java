package cz.vse.pexeso.common.message.payload;

public class StartGameData {

    public int capacity;
    public int cardCount;

    public StartGameData(int capacity, int cardCount) {
        this.capacity = capacity;
        this.cardCount = cardCount;
    }

}
