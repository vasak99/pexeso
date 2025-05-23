package cz.vse.pexeso.common.message.payload;

public class SendableGame {

    public String id;
    public String name;
    public long creatorId;
    public String creatorName;
    public boolean isStarted;
    public int capacity;
    public int cardCount;


    public SendableGame() {
    }

    public SendableGame(String id, String name, long creatorId, String creatorName, boolean isStarted, int capacity, int cardCount) {
        this.id = id;
        this.name = name;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.isStarted = isStarted;
        this.capacity = capacity;
        this.cardCount = cardCount;
    }
}
