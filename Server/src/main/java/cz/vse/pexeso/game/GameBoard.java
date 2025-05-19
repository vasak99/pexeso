package cz.vse.pexeso.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.utils.Rand;
import cz.vse.pexeso.exceptions.CardsException;
import cz.vse.pexeso.exceptions.DeckException;

public class GameBoard {

    private static final Logger log = LoggerFactory.getLogger(GameBoard.class);

    private Card[][] matrix;
    private Deck deck;

    public GameBoard(int cardCount) throws CardsException, DeckException {
        log.info("Attempting to create new game board");

        this.deck = new Deck();

        if(cardCount > deck.deckSize() * 2) {
            throw new CardsException("Not enough cards in deck");
        }

        int[] shuff = this.generateShuffle(cardCount, this.deck.deckSize());

        int[] dimensions = generateDimensions(cardCount);
        this.matrix = new Card[dimensions[0]][dimensions[1]];

        for(int i = 0; i < shuff.length; i++) {
            int row = (int) ((float) i / (float) dimensions[1]);
            int col = i % dimensions[1];
            this.matrix[row][col] = new Card(shuff[i], this.deck.getImage(shuff[i]));
        }

        log.info("Game board created successfully");
    }

    private int[] generateDimensions(int cardCount) {
        int[] dims = new int[] {cardCount, cardCount};

        for(int i = 1; i <= cardCount; i++) {
            int ct = i*i;
            if(ct >= cardCount) {
                if(i*(i - 1) >= cardCount) {
                    dims[0] = i;
                    dims[1] = i - 1;
                } else {
                    dims[0] = dims[1] = i;
                }
                break;
            }
        }

        return dims;
    }

    private int[] generateShuffle(int cardCount, int deckSize) {
        int[] ret = new int[cardCount];
        Set<Integer> ord = new HashSet<Integer>();

        while(ord.size() < cardCount/2) {
            int id = Rand.between(0, deckSize - 1);
            ord.add(id);
        }

        ArrayList<Integer> openPositions = new ArrayList<Integer>();
        for(int i = 0; i < cardCount; i++) {
            openPositions.add(i);
        }

        for(var cc : ord) {
            int opindex1 = Rand.between(0, openPositions.size() - 1);
            int index1 = openPositions.remove(opindex1);

            int opindex2 = Rand.between(0, openPositions.size() - 1);
            int index2 = openPositions.remove(opindex2);

            ret[index1] = cc;
            ret[index2] = cc;
        }

        return ret;
    }

    public Card revealCard(int row, int col) {
        if(this.matrix[row][col] == null) {
            return null;
        }
        this.matrix[row][col].reveal();
        return this.matrix[row][col];
    }

    public void hideAll() {
        for(var row : this.matrix) {
            for(var col : row) {
                if(col != null) {
                    col.hide();
                }
            }
        }
    }

    public void removePair(int id) {
        for(int i = 0; i < this.matrix.length; i++) {
            for(int j = 0; j < this.matrix[i].length; j++) {
                Card card = this.matrix[i][j];
                if(card.getId() == id) {
                    this.matrix[i][j] = null;
                }
            }
        }
    }

    public void removeCard(int row, int col) {
        this.matrix[row][col] = null;
    }

    public String getAsData() {
        String data = "";
        for(int i = 0; i < this.matrix.length; i++) {
            data += "[";
            for(int j = 0; j < this.matrix[i].length; j++) {
                if(this.matrix[i][j] == null) {
                    data += "null";
                }
                else if(this.matrix[i][j].getRevealed()) {
                    data += this.matrix[i][j].getImage();
                } else {
                    data += "x";
                }

                if(j < this.matrix[i].length - 1) {
                    data += ",";
                }
            }
            data += "]";
            if(i < this.matrix.length - 1) {
                data += ";";
            }
        }

        return data;
    }

    public boolean allRevealed() {
        for(int i = 0; i < this.matrix.length; i++) {
            for(int j = 0; j < this.matrix[i].length; j ++) {
                if(this.matrix[i][j] != null) {
                    return false;
                }
            }
        }
        return true;
    }

}
