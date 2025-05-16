package cz.vse.pexeso.common.message.payload;

public class SendablePlayer {

    public String name;
    public boolean status;

    public SendablePlayer(String name, boolean status) {
        this.name = name;
        this.status = status;
    }

}
