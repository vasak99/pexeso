package cz.vse.pexeso.view;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Visual representation of a game board. Parses a string to generate and lay out GameCards in a grid.
 *
 * @author kott10
 * @version June 2025
 */
public class Board extends GridPane {
    private static final Logger log = LoggerFactory.getLogger(Board.class);

    private String gameBoardString;
    private final List<GameCard> gameCardList = new ArrayList<>();
    private int numberOfRows;
    private int numberOfColumns;
    public static final int gap = 15;

    /**
     * Constructs a Board by parsing the given string and generating GameCards.
     *
     * @param gameBoardString semicolon-separated rows of card data
     */
    public Board(String gameBoardString) {
        this.gameBoardString = gameBoardString;
        log.info("Generating Board from string");
        generate();
        setup();
    }

    /**
     * Configures layout gaps and alignment for the grid.
     */
    private void setup() {
        setHgap(gap);
        setVgap(gap);
        setAlignment(Pos.CENTER);
    }

    /**
     * Parses gameBoardString and populates gameCardList.
     */
    private void generate() {
        gameCardList.clear();
        List<GameCard> parsedCards = parseString();
        for (GameCard gameCard : parsedCards) {
            gameCardList.add(gameCard);
            if (gameCard.getStatus() != GameCard.Status.NULL) {
                add(gameCard, gameCard.getColumn(), gameCard.getRow());
            }
        }
        log.info("Generated {} GameCard nodes (rows: {}, cols: {})", parsedCards.size(), numberOfRows, numberOfColumns);
    }

    /**
     * Updates the board based on a new gameBoardString.
     * Only changes cards whose status differs from the previous state, preserving completed cards.
     *
     * @param color       color string to apply when marking completed cards
     * @param currentTurn set of GameCards currently selected this turn
     */
    private void update(String color, Set<GameCard> currentTurn) {
        List<GameCard> updatedCardList = parseString();
        for (int i = 0; i < gameCardList.size(); i++) {
            GameCard current = gameCardList.get(i);
            GameCard updated = updatedCardList.get(i);

            if (current.getStatus() == GameCard.Status.NULL || current.getStatus() == GameCard.Status.COMPLETED) {
                continue;
            }

            if (current.getStatus() == updated.getStatus()) {
                continue;
            }

            switch (updated.getStatus()) {
                case REVEALED -> {
                    current.reveal(updated.getImageName());
                    log.debug("Revealed card at ({}, {}) with image {}", current.getRow(), current.getColumn(), updated.getImageName());
                }
                case HIDDEN -> {
                    current.hide();
                    currentTurn.clear();
                    log.debug("Hid card at ({}, {})", current.getRow(), current.getColumn());
                }
                case NULL -> {
                    current.markCompleted(color);
                    currentTurn.clear();
                    log.debug("Marked card at ({}, {}) as completed", current.getRow(), current.getColumn());
                }
                default -> {
                }
            }
        }
    }

    /**
     * Parses gameBoardString into a list of GameCard instances.
     *
     * @return a new list of GameCards representing each board cell
     */
    private List<GameCard> parseString() {
        List<GameCard> parsed = new ArrayList<>();
        try {
            String[] rows = this.gameBoardString.split(";");
            this.numberOfRows = rows.length;
            for (int row = 0; row < rows.length; row++) {
                String cleaned = rows[row].replace("[", "").replace("]", "");

                String[] cards = cleaned.split(",");
                this.numberOfColumns = cards.length;

                for (int column = 0; column < cards.length; column++) {
                    String card = cards[column].trim();
                    GameCard gameCard = switch (card) {
                        case "x" -> new GameCard(GameCard.Status.HIDDEN, row, column);
                        case "null" -> new GameCard(GameCard.Status.NULL, row, column);
                        default -> new GameCard(GameCard.Status.REVEALED, row, column, card);
                    };
                    parsed.add(gameCard);
                }
            }
        } catch (Exception e) {
            log.error("Error parsing game board string: {}", gameBoardString, e);
        }
        return parsed;
    }

    /**
     * Sets a new board string, then calls update() to adjust existing GameCards.
     *
     * @param gameBoardString new String representation
     * @param color           color for completed cards
     * @param currentTurn     current turn set
     */
    public void setGameBoardString(String gameBoardString, String color, Set<GameCard> currentTurn) {
        this.gameBoardString = gameBoardString;
        update(color, currentTurn);
        log.info("Board string updated and UI refreshed");
    }

    /**
     * @return current GameCard list
     */
    public List<GameCard> getGameCardList() {
        return gameCardList;
    }

    /**
     * @return the number of rows parsed from the board string
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * @return the number of columns parsed from the board string
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }
}