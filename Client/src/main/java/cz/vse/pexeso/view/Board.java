package cz.vse.pexeso.view;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class Board extends GridPane {

    private String gameRoomString;
    private final List<GameCard> gameCardList;

    public Board(String gameBoard) {
        this.gameCardList = new ArrayList<>();
        this.gameRoomString = gameBoard;
        generate(this.gameRoomString);
        setup();
    }

    private void generate(String gameBoard) {
        String[] rows = gameBoard.split(";");
        for (int row = 0; row < rows.length; row++) {
            String cleaned = rows[row].replace("[", "").replace("]", "");

            String[] cards = cleaned.split(",");

            for (int column = 0; column < cards.length; column++) {
                String card = cards[column];
                if (card.equals("null")) {
                    continue;
                }
                GameCard gameCard = new GameCard(row, column, null);

                gameCardList.add(gameCard);
                add(gameCard, column, row);
            }
        }
    }

    private void setup() {
        setHgap(15);
        setVgap(15);
        setAlignment(Pos.CENTER);
    }

    public String getGameRoomString() {
        return gameRoomString;
    }

    public void setGameRoomString(String gameRoomString) {
        this.gameRoomString = gameRoomString;
    }

    public List<GameCard> getGameCardList() {
        return gameCardList;
    }
}
