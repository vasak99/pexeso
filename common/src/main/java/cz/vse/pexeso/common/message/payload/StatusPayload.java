package cz.vse.pexeso.common.message.payload;

public class StatusPayload {
    private int player1Score;
    private int player2Score;

    public StatusPayload(int player1Score, int player2Score) {
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }
}
