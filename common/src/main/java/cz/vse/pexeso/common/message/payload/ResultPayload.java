package cz.vse.pexeso.common.message.payload;

public class ResultPayload {
    private int player1Score;
    private int player2Score;
    private String result; // win/draw
    private String winningPlayer; // null if draw

    public ResultPayload(int player1Score, int player2Score, String result, String winningPlayer) {
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.result = result;
        this.winningPlayer = winningPlayer;
    }
}
