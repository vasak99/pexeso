package cz.vse.pexeso.common.message.payload;

public class PairPayload implements MessagePayload {
    private boolean isPair;

    public PairPayload(boolean isPair) {
        this.isPair = isPair;
    }
}
