package cz.vse.pexeso.common.message.payload;

public class SendablePlayer {

    public String name;
    public boolean status;

    public int score;

    public SendablePlayer(String name, boolean status, int score) {
        this.name = name;
        this.status = status;
        this.score = score;
    }

}
