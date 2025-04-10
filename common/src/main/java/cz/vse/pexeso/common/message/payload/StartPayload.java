package cz.vse.pexeso.common.message.payload;

public class StartPayload {
    private String player1Name;
    private String player2Name;
    private String[] shuffledCards;
    private int startingPlayer;

    public StartPayload(String player1Name, String player2Name, String[] shuffledCards, int startingPlayer) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.shuffledCards = shuffledCards;
        this.startingPlayer = startingPlayer;
    }
}
