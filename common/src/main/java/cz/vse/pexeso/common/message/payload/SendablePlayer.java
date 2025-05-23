package cz.vse.pexeso.common.message.payload;

public class SendablePlayer {

    public long id;
    public String name;
    public boolean status;

    public int score;

    public SendablePlayer() {
    }

    public SendablePlayer(long id, String name, boolean status, int score) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.score = score;
    }

}
