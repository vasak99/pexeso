package cz.vse.pexeso.view;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Board extends GridPane {

    private String gameBoardString;
    private final List<GameCard> gameCardList;
    private int numberOfRows;
    private int numberOfColumns;
    public static final int gap = 15;

    public Board(String gameBoardString) {
        this.gameCardList = new ArrayList<>();
        this.gameBoardString = gameBoardString;
        generate();
        setup();
    }

    private void generate() {
        gameCardList.clear();

        for (GameCard gameCard : parseString()) {
            gameCardList.add(gameCard);
            if (gameCard.getStatus() != GameCard.Status.NULL) {
                add(gameCard, gameCard.getColumn(), gameCard.getRow());
            }
        }
    }

    private void update(String color, Set<GameCard> currentTurn) {
        List<GameCard> updatedCardList = parseString();

        for (int i = 0; i < gameCardList.size(); i++) {
            GameCard current = gameCardList.get(i);
            GameCard update = updatedCardList.get(i);

            if (current.getStatus() == GameCard.Status.COMPLETED) {
                continue;
            }

            if (current.getStatus() == update.getStatus()) {
                continue;
            }

            if (current.getStatus() == GameCard.Status.HIDDEN && update.getStatus() == GameCard.Status.REVEALED) {
                current.reveal(update.getImageName());
            }

            if (current.getStatus() == GameCard.Status.REVEALED && update.getStatus() == GameCard.Status.HIDDEN) {
                current.hide();
                currentTurn.clear();
            }

            if (current.getStatus() == GameCard.Status.REVEALED && update.getStatus() == GameCard.Status.NULL) {
                current.markCompleted(color);
                currentTurn.clear();
            }
        }
    }

    private void setup() {
        setHgap(gap);
        setVgap(gap);
        setAlignment(Pos.CENTER);
    }

    private List<GameCard> parseString() {
        List<GameCard> parsed = new ArrayList<>();
        String[] rows = this.gameBoardString.split(";");
        this.numberOfRows = rows.length;
        for (int row = 0; row < rows.length; row++) {
            String cleaned = rows[row].replace("[", "").replace("]", "");

            String[] cards = cleaned.split(",");
            this.numberOfColumns = cards.length;

            for (int column = 0; column < cards.length; column++) {
                String card = cards[column];
                GameCard gameCard;
                if (card.equals("x")) {
                    gameCard = new GameCard(GameCard.Status.HIDDEN, row, column);
                } else if (!card.equals("null")) {
                    gameCard = new GameCard(GameCard.Status.REVEALED, row, column, card);
                } else {
                    gameCard = new GameCard(GameCard.Status.NULL, row, column);
                }
                parsed.add(gameCard);
            }
        }
        return parsed;
    }

    public void setGameBoardString(String gameBoardString, String color, Set<GameCard> currentTurn) {
        this.gameBoardString = gameBoardString;
        update(color, currentTurn);
    }

    public List<GameCard> getGameCardList() {
        return gameCardList;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }
}
