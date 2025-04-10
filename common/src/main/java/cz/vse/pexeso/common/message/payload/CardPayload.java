package cz.vse.pexeso.common.message.payload;

public class CardPayload implements MessagePayload {
    private String card;
    private int order; // whether the card is first or second in the pair

    public CardPayload(String card, int order) {
        this.card = card;
        this.order = order;
    }
}
