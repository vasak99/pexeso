package cz.vse.pexeso.common.message.payload;

public class InvalidMovePayload implements MessagePayload {

    public String message;

    public InvalidMovePayload(String message) {
        this.message = message;
    }

	@Override
	public String toSendable() {
        return this.message;
	}


}
