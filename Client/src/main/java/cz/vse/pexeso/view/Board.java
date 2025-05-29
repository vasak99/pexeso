package cz.vse.pexeso.view;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class Board extends GridPane {

    private String gameBoardString;
    private final List<GameCard> gameCardList;

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

    private void update(String color) {
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
            }

            if (current.getStatus() == GameCard.Status.REVEALED && update.getStatus() == GameCard.Status.NULL) {
                current.markCompleted(color);
            }
        }
    }

    private void setup() {
        setHgap(15);
        setVgap(15);
        setAlignment(Pos.CENTER);
    }

    private List<GameCard> parseString() {
        List<GameCard> parsed = new ArrayList<>();
        String[] rows = this.gameBoardString.split(";");
        for (int row = 0; row < rows.length; row++) {
            String cleaned = rows[row].replace("[", "").replace("]", "");

            String[] cards = cleaned.split(",");

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

    public void setGameBoardString(String gameBoardString, String color) {
        this.gameBoardString = gameBoardString;
        update(color);
    }

    public List<GameCard> getGameCardList() {
        return gameCardList;
    }
}
